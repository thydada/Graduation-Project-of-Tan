package com.example.gpot.service;

import com.example.gpot.entity.Message;
import com.example.gpot.entity.User;
import com.example.gpot.repository.MessageRepository;
import com.example.gpot.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserRepository userRepository;

    public List<Message> getUserMessages(Long userId) {
        return messageRepository.findByReceiverIdAndReceiverTypeOrderBySendTimeDesc(userId, "user");
    }

    public List<Message> getUnreadMessages(Long userId) {
        return messageRepository.findByReceiverIdAndReceiverTypeAndStatusOrderBySendTimeDesc(userId, "user", "未读");
    }

    public long getUnreadMessageCount(Long userId) {
        return messageRepository.countByReceiverIdAndReceiverTypeAndStatus(userId, "user", "未读");
    }

    @Transactional
    public Message markAsRead(Long messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("消息不存在"));
        message.setStatus("已读");
        message.setReadTime(LocalDateTime.now());
        return messageRepository.save(message);
    }

    @Transactional
    public Message createMessage(String title, String content, String messageType,
                                 String senderType, Long senderId, Long receiverId,
                                 Long warehouseId) {
        Message message = new Message();
        message.setTitle(title);
        message.setContent(content);
        message.setMessageType(messageType);
        message.setSenderType(senderType);
        message.setSenderId(senderId);
        message.setReceiverType("user");
        message.setReceiverId(receiverId);
        message.setWarehouseId(warehouseId);
        message.setStatus("未读");
        message.setSendTime(LocalDateTime.now());
        return messageRepository.save(message);
    }

    @Transactional
    public void sendPickupNotification(Long userId, String trackingNumber, String pickupCode,
                                       Long shelfId, Integer shelfLayer, Long warehouseId,
                                       String senderType, Long senderId) {
        String title = "取件提醒";
        String content = String.format(
            "您的快递已入库，请及时取件。\n\n" +
            "快递单号：%s\n" +
            "取件码：%s\n" +
            "货架位置：货架%d - 第%d层\n\n" ,
            trackingNumber, pickupCode, shelfId, shelfLayer
        );

        createMessage(title, content, "取件提醒", senderType, senderId, userId, warehouseId);
    }

    @Transactional
    public int markAllAsRead(Long userId) {
        List<Message> unreadMessages = messageRepository.findByReceiverIdAndReceiverTypeAndStatus(
            userId, "user", "未读"
        );
        LocalDateTime now = LocalDateTime.now();
        for (Message message : unreadMessages) {
            message.setStatus("已读");
            message.setReadTime(now);
        }
        messageRepository.saveAll(unreadMessages);
        return unreadMessages.size();
    }

    @Transactional
    public int sendMessageToAllUsers(String title, String content, String messageType,
                                     String senderType, Long senderId, Long warehouseId) {
        List<User> allUsers = userRepository.findAll();
        LocalDateTime now = LocalDateTime.now();
        int count = 0;

        for (User user : allUsers) {
            Message message = new Message();
            message.setTitle(title);
            message.setContent(content);
            message.setMessageType(messageType);
            message.setSenderType(senderType);
            message.setSenderId(senderId);
            message.setReceiverType("user");
            message.setReceiverId(user.getId());
            message.setWarehouseId(warehouseId);
            message.setStatus("未读");
            message.setSendTime(now);
            messageRepository.save(message);
            count++;
        }

        return count;
    }
}

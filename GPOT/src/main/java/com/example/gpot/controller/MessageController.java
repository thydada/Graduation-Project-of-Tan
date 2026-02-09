package com.example.gpot.controller;

import com.example.gpot.dto.ApiResponse;
import com.example.gpot.entity.Message;
import com.example.gpot.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @GetMapping("/messages/user/{userId}")
    public ResponseEntity<ApiResponse<List<Message>>> getUserMessages(@PathVariable Long userId) {
        try {
            List<Message> messages = messageService.getUserMessages(userId);
            return ResponseEntity.ok(ApiResponse.success("查询成功", messages));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("查询过程中发生错误：" + e.getMessage()));
        }
    }

    @GetMapping("/messages/user/{userId}/unread")
    public ResponseEntity<ApiResponse<List<Message>>> getUnreadMessages(@PathVariable Long userId) {
        try {
            List<Message> messages = messageService.getUnreadMessages(userId);
            return ResponseEntity.ok(ApiResponse.success("查询成功", messages));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("查询过程中发生错误：" + e.getMessage()));
        }
    }

    @GetMapping("/messages/user/{userId}/unread-count")
    public ResponseEntity<ApiResponse<Long>> getUnreadMessageCount(@PathVariable Long userId) {
        try {
            long count = messageService.getUnreadMessageCount(userId);
            return ResponseEntity.ok(ApiResponse.success("查询成功", count));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("查询过程中发生错误：" + e.getMessage()));
        }
    }

    @PutMapping("/messages/{messageId}/read")
    public ResponseEntity<ApiResponse<Message>> markAsRead(@PathVariable Long messageId) {
        try {
            Message message = messageService.markAsRead(messageId);
            return ResponseEntity.ok(ApiResponse.success("标记已读成功", message));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("标记已读过程中发生错误：" + e.getMessage()));
        }
    }

    @PutMapping("/messages/user/{userId}/read-all")
    public ResponseEntity<ApiResponse<Map<String, Object>>> markAllAsRead(@PathVariable Long userId) {
        try {
            int count = messageService.markAllAsRead(userId);
            Map<String, Object> result = new HashMap<>();
            result.put("count", count);
            return ResponseEntity.ok(ApiResponse.success("一键已读成功，共标记 " + count + " 条消息", result));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("一键已读过程中发生错误：" + e.getMessage()));
        }
    }

    @PostMapping("/messages/send-to-all")
    public ResponseEntity<ApiResponse<Map<String, Object>>> sendMessageToAllUsers(
            @RequestBody Map<String, Object> request) {
        try {
            String title = (String) request.get("title");
            String content = (String) request.get("content");
            String messageType = (String) request.get("messageType");
            String senderType = (String) request.get("senderType");
            Long senderId = request.get("senderId") != null ? 
                ((Number) request.get("senderId")).longValue() : null;
            Long warehouseId = request.get("warehouseId") != null ? 
                ((Number) request.get("warehouseId")).longValue() : null;

            if (title == null || title.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("消息标题不能为空"));
            }
            if (content == null || content.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("消息内容不能为空"));
            }
            if (messageType == null || messageType.trim().isEmpty()) {
                messageType = "系统公告";
            }
            if (senderType == null || senderType.trim().isEmpty()) {
                senderType = "employee";
            }

            int count = messageService.sendMessageToAllUsers(
                title, content, messageType, senderType, senderId, warehouseId
            );

            Map<String, Object> result = new HashMap<>();
            result.put("count", count);
            return ResponseEntity.ok(ApiResponse.success("消息发送成功，共发送给 " + count + " 个用户", result));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("发送消息过程中发生错误：" + e.getMessage()));
        }
    }
}

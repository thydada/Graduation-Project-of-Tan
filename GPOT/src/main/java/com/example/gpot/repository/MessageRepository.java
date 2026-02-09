package com.example.gpot.repository;

import com.example.gpot.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findByReceiverIdAndReceiverTypeOrderBySendTimeDesc(Long receiverId, String receiverType);

    List<Message> findByReceiverIdAndReceiverTypeAndStatusOrderBySendTimeDesc(Long receiverId, String receiverType, String status);

    long countByReceiverIdAndReceiverTypeAndStatus(Long receiverId, String receiverType, String status);

    List<Message> findByReceiverIdAndReceiverTypeAndStatus(Long receiverId, String receiverType, String status);
}

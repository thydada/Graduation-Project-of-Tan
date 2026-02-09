package com.example.gpot.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "message")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "message_type", nullable = false, length = 20)
    private String messageType;

    @Column(name = "sender_type", length = 20)
    private String senderType;

    @Column(name = "sender_id")
    private Long senderId;

    @Column(name = "receiver_type", length = 20)
    private String receiverType;

    @Column(name = "receiver_id")
    private Long receiverId;

    @Column(name = "warehouse_id")
    private Long warehouseId;

    @Column(length = 20, columnDefinition = "VARCHAR(20) DEFAULT '未读'")
    private String status;

    @Column(name = "send_time", columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime sendTime;

    @Column(name = "read_time")
    private LocalDateTime readTime;
}

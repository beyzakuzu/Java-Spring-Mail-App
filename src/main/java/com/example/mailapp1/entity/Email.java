package com.example.mailapp1.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "emails")
public class Email {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String toList;
    private String ccList;
    private String bccList;
    private String subject;
    private String body;
    private ZonedDateTime sendTime;
    private boolean isDraft;
    private String senderEmail;

    @Enumerated(EnumType.STRING)
    private EmailStatus status;

    public enum EmailStatus {
        WAITING,
        SENT,
        CANCELED
    }

}

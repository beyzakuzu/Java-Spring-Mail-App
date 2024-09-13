package com.example.mailapp1.repository;

import com.example.mailapp1.entity.Email;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.ZonedDateTime;
import java.util.List;

public interface EmailRepository extends JpaRepository<Email, Long> {

    List<Email> findAllByStatus(Email.EmailStatus status);
    List<Email> findAllByStatusAndSendTimeBefore(Email.EmailStatus status, ZonedDateTime time);
}
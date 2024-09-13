package com.example.mailapp1.config;

import com.example.mailapp1.entity.Email;
import com.example.mailapp1.repository.EmailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.time.ZonedDateTime;
import java.util.List;


public class EmailProcessor implements Runnable {

    private final List<Email> emails;
    private final int startIndex;
    private final int endIndex;
    private final EmailRepository emailRepository;
    private final ZonedDateTime now;

    public EmailProcessor(List<Email> emails, int startIndex, int endIndex, EmailRepository emailRepository, ZonedDateTime now) {
        this.emails = emails;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
        this.emailRepository = emailRepository;
        this.now = now;
    }

    @Override
    public void run() {
        processBatch();
    }

    private void processBatch() {
        for (int i = startIndex; i < endIndex; i++) {
            Email email = emails.get(i);
            if (email.getSendTime() != null && email.getSendTime().isBefore(now.minusMinutes(1))) {
                email.setStatus(Email.EmailStatus.CANCELED);
                emailRepository.save(email);
            }
        }
    }
}
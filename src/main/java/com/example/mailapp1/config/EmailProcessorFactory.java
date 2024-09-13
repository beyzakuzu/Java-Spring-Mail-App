package com.example.mailapp1.config;


import com.example.mailapp1.entity.Email;
import com.example.mailapp1.repository.EmailRepository;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.List;

@Component
public class EmailProcessorFactory {

    public EmailProcessor createProcessor(List<Email> emails, int startIndex, int endIndex, EmailRepository emailRepository, ZonedDateTime now) {
        return new EmailProcessor(emails, startIndex, endIndex, emailRepository, now);
    }
}
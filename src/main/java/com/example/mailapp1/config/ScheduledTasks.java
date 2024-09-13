package com.example.mailapp1.config;


import com.example.mailapp1.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledTasks {

    @Autowired
    private EmailService emailService;

    @Scheduled(fixedRate = 60000)
    public void processWaitingEmails() {
        emailService.processWaitingEmails();
    }
}

package com.example.mailapp1.service;
import java.io.File;
import java.io.IOException;
import java.util.*;

import com.example.mailapp1.config.EmailProcessor;
import com.example.mailapp1.config.EmailProcessorFactory;
import com.example.mailapp1.config.EncryptionUtil;
import com.example.mailapp1.entity.Email;
import com.example.mailapp1.entity.User;
import com.example.mailapp1.repository.EmailRepository;
import com.example.mailapp1.repository.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.context.ApplicationContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.ZonedDateTime;
import java.util.concurrent.Executor;

@Service
public class EmailService {

    @Autowired
    private EmailRepository emailRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private Executor taskExecutor;

    @Autowired
    private EmailProcessorFactory emailProcessorFactory;

    @Autowired
    private ApplicationContext context;

    public Email sendEmail(Email email, String senderEmail) {
        User user = userRepository.findByEmail(senderEmail);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        validateRecipients(email);

        email.setStatus(Email.EmailStatus.WAITING);
        email.setSendTime(ZonedDateTime.now());
        emailRepository.save(email);

        try {

            String decryptedPassword = EncryptionUtil.decrypt(user.getPassword());

            JavaMailSender mailSender = createMailSender(user.getEmail(), decryptedPassword);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(user.getEmail());

            if (email.getToList() != null && !email.getToList().isEmpty()) {
                helper.setTo(email.getToList().split(","));
            } else {


                helper.setBcc(email.getBccList().split(","));
            }


            if (email.getCcList() != null && !email.getCcList().isEmpty()) {
                helper.setCc(email.getCcList().split(","));
            }

            helper.setSubject(email.getSubject());
            helper.setText(email.getBody(), true);



            mailSender.send(message);
            email.setStatus(Email.EmailStatus.SENT);
        } catch (Exception e) {
            e.printStackTrace();
            email.setStatus(Email.EmailStatus.CANCELED);
            throw new RuntimeException("Failed to send email: " + e.getMessage());
        }

        return emailRepository.save(email);
    }
    private void validateRecipients(Email email) {

        checkRecipientList(email.getToList());
        checkRecipientList(email.getCcList());
        checkRecipientList(email.getBccList());
    }

    private void checkRecipientList(String recipientList) {
        if (recipientList != null && !recipientList.isEmpty()) {
            String[] recipients = recipientList.split(",");
            for (String recipient : recipients) {
                if (userRepository.findByEmail(recipient.trim()) == null) {
                    throw new RuntimeException("Alıcı kullanıcı bulunamadı: " + recipient);
                }
            }
        }
    }

    private JavaMailSender createMailSender(String email, String password) {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        mailSender.setJavaMailProperties(props);
        mailSender.setUsername(email);
        mailSender.setPassword(password);

        return mailSender;
    }
    public Email saveDraft(Email email, String senderEmail) {
        User user = userRepository.findByEmail(senderEmail);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        email.setStatus(Email.EmailStatus.WAITING);
        email.setSendTime(ZonedDateTime.now());
        emailRepository.save(email);
        email.setDraft(true);

        return email;
    }




    public void processWaitingEmails() {


        List<Email> waitingEmails = emailRepository.findAllByStatus(Email.EmailStatus.WAITING);
        if (waitingEmails.isEmpty()) {
            return;
        }
        ZonedDateTime now = ZonedDateTime.now();
        int batchSize = 10;
        int totalEmails = waitingEmails.size();
        int threadCount = (int) Math.ceil((double) totalEmails / batchSize);

        for (int i = 0; i < threadCount; i++) {
            int startIndex = i * batchSize;
            int endIndex = Math.min(startIndex + batchSize, totalEmails);

            EmailProcessor emailProcessor = emailProcessorFactory.createProcessor(waitingEmails, startIndex, endIndex, emailRepository, now);
            taskExecutor.execute(emailProcessor);
        }


        /* Map<Thread, StackTraceElement[]> allStackTraces = Thread.getAllStackTraces();
        for (Thread thread : allStackTraces.keySet()) {
            System.out.println("Thread: " + thread.getName());
        } */


    }

    public void approveEmail(Long id) {

        Email email = emailRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Email not found"));


        if (email.getStatus() == Email.EmailStatus.SENT || email.getStatus() == Email.EmailStatus.CANCELED) {
            throw new RuntimeException("Email has already been processed");
        }


        User sender = userRepository.findByEmail(email.getSenderEmail());
        if (sender == null) {
            throw new RuntimeException("Sender not found");
        }

        validateRecipients(email);


        try {

            String decryptedPassword = EncryptionUtil.decrypt(sender.getPassword());


            JavaMailSender mailSender = createMailSender(sender.getEmail(), decryptedPassword);


            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(sender.getEmail());
            helper.setTo(email.getToList().split(","));
            if (email.getCcList() != null && !email.getCcList().isEmpty()) {
                helper.setCc(email.getCcList().split(","));
            }
            if (email.getBccList() != null && !email.getBccList().isEmpty()) {
                helper.setBcc(email.getBccList().split(","));
            }
            helper.setSubject(email.getSubject());
            helper.setText(email.getBody(), true);


            mailSender.send(message);


            email.setStatus(Email.EmailStatus.SENT);
        } catch (Exception e) {
            e.printStackTrace();

            email.setStatus(Email.EmailStatus.CANCELED);
            throw new RuntimeException("Failed to send email: " + e.getMessage());
        }


        emailRepository.save(email);
    }
    public void rejectEmail(Long id) {
        Email email = emailRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Email not found"));
        email.setStatus(Email.EmailStatus.CANCELED);
        emailRepository.save(email);
    }

    public void deleteEmail(Long id) {
        Email email= emailRepository.findById(id)
                .orElseThrow( () -> new RuntimeException("Email not found"));
        emailRepository.deleteById(email.getId());
    }



    public List<Email> getWaitingEmails() {
        return emailRepository.findAllByStatus(Email.EmailStatus.WAITING);
    }
}


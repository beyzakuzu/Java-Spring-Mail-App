package com.example.mailapp1.controller;

import com.example.mailapp1.entity.Email;
import com.example.mailapp1.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/emails")
public class EmailController {

    @Autowired
    private EmailService emailService;

    @PostMapping("/send")
    public ResponseEntity<?> sendEmail(@RequestBody Email emailRequest,
                                       @RequestParam String senderEmail) {
        try {
            Email sentEmail = emailService.sendEmail(emailRequest, senderEmail);
            return ResponseEntity.ok(sentEmail);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
    @PostMapping("/draft")
    public ResponseEntity<?> saveDraft(@RequestBody Email emailRequest,
                                       @RequestParam String senderEmail) {
        try {
            Email draftEmail = emailService.saveDraft(emailRequest, senderEmail);
            return ResponseEntity.ok(draftEmail);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
    @PostMapping("/process-waiting")
    public ResponseEntity<?> processWaitingEmails() {
        try {
            emailService.processWaitingEmails();
            return ResponseEntity.ok("Waiting emails processed successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping("/approve/{id}")
    public String approveEmail(@PathVariable Long id) {
        try {
            emailService.approveEmail(id);
            return "Email approved and sent successfully.";
        } catch (RuntimeException e) {
            return "Error: " + e.getMessage();
        }
    }


    @PostMapping("/{id}/reject")
    public ResponseEntity<?> rejectEmail(@PathVariable Long id) {
        try {
            emailService.rejectEmail(id);
            return ResponseEntity.ok("Email rejected successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}/delete")
    public ResponseEntity<?> deleteEmail(@PathVariable Long id){
        try {
            emailService.deleteEmail(id);
            return ResponseEntity.ok("E-posta başarıyla silindi");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/waiting")
    public ResponseEntity<List<Email>> getWaitingEmails() {
        List<Email> waitingEmails = emailService.getWaitingEmails();
        return ResponseEntity.ok(waitingEmails);
    }


}


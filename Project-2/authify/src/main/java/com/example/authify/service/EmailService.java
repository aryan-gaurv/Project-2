package com.example.authify.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    final JavaMailSender mailSender;
    public void sendEmail(String to, String name) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo(to);
        simpleMailMessage.setSubject("WELCOME TO OURS PLATFORM");
        simpleMailMessage.setText("Hello " + name + ",\n\n Thanks for Registration with us !!!\n\n");
        mailSender.send(simpleMailMessage);
    }

    public void sendResetOtp(String email,String otp) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo(email);
        simpleMailMessage.setSubject("RESET OTP");
        simpleMailMessage.setText("Your otp for resetting your password is: \t"+otp);
        mailSender.send(simpleMailMessage);
    }

    public void sendOtpEmail(String email,String otp) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo(email);
        simpleMailMessage.setSubject("Verification OTP");
        simpleMailMessage.setText("Your otp for verification your password is: \t"+otp);
        mailSender.send(simpleMailMessage);
    }
}

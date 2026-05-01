package com.vinay.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Async
    public void sendRegistrationEmail(String toEmail, String name) {

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            String subject = "Welcome to Employee Management System";

            String verifyUrl =
                    "http://13.203.79.1:8080/api/auth/verify-account?email=" + toEmail;

            String htmlContent =
                    "<div style='font-family: Arial, sans-serif; padding: 20px;'>"

                    + "<h2 style='color:#2c3e50;'>Welcome to Employee Management System</h2>"

                    + "<p>Hi <b>" + name + "</b>,</p>"

                    + "<p>Your registration was successful 🎉</p>"

                    + "<p>Click below to verify your email:</p>"

                    + "<a href='" + verifyUrl + "' "
                    + "style='display:inline-block; padding:10px 20px; "
                    + "background-color:#28a745; color:white; "
                    + "text-decoration:none; border-radius:5px;'>"
                    + "Verify Email"
                    + "</a>"

                    + "<p style='margin-top:20px;'>After verification, you can login and add employee details.</p>"

                    + "<br/><p>Regards,<br/>"
                    + "<b>Employee Management Team</b></p>"

                    + "<hr/>"

                    + "<small>This is an automated email. Please do not reply.</small>"

                    + "</div>";

            helper.setTo(toEmail);
            helper.setFrom("dasarivinay12345@gmail.com");
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            mailSender.send(message);

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    @Async
    public void sendResetPasswordEmail(String toEmail, String name, String resetLink) {

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            String subject = "Reset Your Password";

            String htmlContent =
                    "<div style='font-family: Arial, sans-serif; padding: 20px;'>"

                    + "<h2 style='color:#e74c3c;'>Reset Password</h2>"

                    + "<p>Hi <b>" + name + "</b>,</p>"

                    + "<p>We received a request to reset your password.</p>"

                    + "<p>Click the button below to reset your password:</p>"

                    + "<a href='" + resetLink + "' "
                    + "style='display:inline-block; padding:10px 20px; "
                    + "background-color:#dc3545; color:white; "
                    + "text-decoration:none; border-radius:5px;'>"
                    + "Reset Password"
                    + "</a>"

                    + "<p style='margin-top:20px;'>This link will expire in 15 minutes.</p>"

                    + "<p>If you did not request this, please ignore this email.</p>"

                    + "<br/><p>Regards,<br/>"
                    + "<b>Employee Management Team</b></p>"

                    + "<hr/>"

                    + "<small>This is an automated email. Please do not reply.</small>"

                    + "</div>";

            helper.setTo(toEmail);
            helper.setFrom("dasarivinay12345@gmail.com");
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            mailSender.send(message);

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
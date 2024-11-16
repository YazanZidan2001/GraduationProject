package com.example.GraduationProject.Core.Services;

import com.example.GraduationProject.Common.Entities.User;
import com.example.GraduationProject.Common.Entities.VerificationCode;
import com.example.GraduationProject.Core.Repositories.UserRepository;
import com.example.GraduationProject.Core.Repositories.VerificationCodeRepository;
import com.sendgrid.*;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class VerificationService {
    //   # Recovery code: HJCSDU2MCYFGPTQ4DGS4CKBL
    private final UserRepository userRepository;
    private final VerificationCodeRepository verificationCodeRepository;

    @Value("${twilio.accountSid}")
    private String twilioAccountSid;

    @Value("${twilio.authToken}")
    private String twilioAuthToken;

    @Value("${twilio.phoneNumber}")
    private String twilioPhoneNumber;

    @Value("${sendgrid.apiKey}")
    private String sendGridApiKey;

    // Initializes Twilio with account SID and auth token
    public void initTwilio() {
        Twilio.init(twilioAccountSid, twilioAuthToken);
    }

    // Generates a 6-digit verification code
    private String generateVerificationCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }

    // Sends verification code based on user preference
    public void sendVerificationCode(User user) throws IOException {
        String code = generateVerificationCode();

        // Save the code in the database
        VerificationCode verificationCode = VerificationCode.builder()
                .code(code)
                .expirationTime(LocalDateTime.now().plusMinutes(5))
                .isUsed(false)
                .user(user)
                .build();
        verificationCodeRepository.save(verificationCode);

        // Send the code via the preferred method
        if ("phone".equalsIgnoreCase(user.getPreferred2faMethod())) {
            sendSmsCode(user.getPhone(), code);
        } else if ("email".equalsIgnoreCase(user.getPreferred2faMethod())) {
            sendEmailCode(user.getEmail(), code);
        }
    }

    // Sends SMS using Twilio
    private void sendSmsCode(String phoneNumber, String code) {
        initTwilio();
        Message message = Message.creator(
                new com.twilio.type.PhoneNumber(phoneNumber),
                new com.twilio.type.PhoneNumber(twilioPhoneNumber),
                "Your verification code is: " + code
        ).create();
        System.out.println("Sent SMS: " + message.getSid());
    }

    // Sends email using SendGrid
    private void sendEmailCode(String email, String code) throws IOException {
        Email from = new Email("yazoonzidan@gmail.com");
        String subject = "Your Verification Code";
        Email to = new Email(email);
        Content content = new Content("text/plain", "Your verification code is: " + code);
        Mail mail = new Mail(from, subject, to, content);

        SendGrid sg = new SendGrid(sendGridApiKey);
        Request request = new Request();
        request.setMethod(Method.POST);
        request.setEndpoint("mail/send");
        request.setBody(mail.build());
        Response response = sg.api(request);
        System.out.println("Sent Email: " + response.getStatusCode());
    }

    // Verifies if a given code is valid for the user
    public boolean verifyCode(User user, String code) {
        Optional<VerificationCode> optionalVerificationCode =
                verificationCodeRepository.findByUserAndIsUsedFalse(user);

        if (optionalVerificationCode.isPresent()) {
            VerificationCode verificationCode = optionalVerificationCode.get();

            // Check if the code matches and is within the expiration time
            if (verificationCode.getCode().equals(code) &&
                    verificationCode.getExpirationTime().isAfter(LocalDateTime.now())) {
                verificationCode.markAsUsed();
                verificationCodeRepository.save(verificationCode);
                return true;
            }
        }
        return false;
    }
}

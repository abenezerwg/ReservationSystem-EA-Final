package edu.miu.cs.cs544.event.listner;

import edu.miu.cs.cs544.domain.User;
import edu.miu.cs.cs544.event.RegistrationCompleteEvent;
import edu.miu.cs.cs544.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
public class RegistrationCompleteEventListner implements ApplicationListener<RegistrationCompleteEvent> {

    @Autowired
    private UserService userService;

    @Override
    public void onApplicationEvent(RegistrationCompleteEvent registrationCompleteEvent) {
        //Create the Verification Token for the user with link

        User user = registrationCompleteEvent.getUser();
        String Token = UUID.randomUUID().toString();
        userService.createVerificationToken(user,Token);
        //Send the email

        String url = registrationCompleteEvent.getAppUrl()
                + "/registrationConfirm?token="
                + Token;
//        sendNewMail(user.getEmail(),"Registration Confirmation","Thank you for registering. Please click on the below link to activate your account."+url);
        //send verification Email
        System.out.println("Click the link to verify your account: " + url);

    }
    @Autowired
    private JavaMailSender mailSender;

//    public void sendNewMail(String to, String subject, String body) {
//        SimpleMailMessage message = new SimpleMailMessage();
//        message.setTo(to);
//        message.setSubject(subject);
//        message.setText(body);
//        mailSender.send(message);
//    }
}

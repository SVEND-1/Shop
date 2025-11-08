package org.example.myshop.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;

@Service
@Transactional
public class EmailSenderService {//TODO ДОБАВИТЬ МНОГОПОТОЧНОСТЬ
    private JavaMailSender javaMailSender;

    @Autowired
    public EmailSenderService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void sendMessage(String to, String subject, String content) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("onlineshopkortex@gmail.com");
        message.setTo(to);
        message.setSubject(subject);
        message.setText(content);

        javaMailSender.send(message);
    }

    public void sendVerification(String to) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("onlineshopkortex@gmail.com");

        Random random = new Random();
        int code = random.nextInt(100000,999999);

        String subject = "Kortex: Ваш код для входа [" + code + "]";
        String content = """
    Добро пожаловать в Kortex!
    
    Ваш код для входа: """ + code + """
    
    Введите этот код на странице подтверждения для завершения входа в ваш аккаунт.
    
    Если вы не запрашивали вход, пожалуйста, проигнорируйте это письмо.
    
    С уважением,
    Команда Kortex
    """;

        message.setTo(to);
        message.setSubject(subject);
        message.setText(content);

        javaMailSender.send(message);
    }
}

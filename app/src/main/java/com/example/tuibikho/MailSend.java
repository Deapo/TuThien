package com.example.tuibikho;

import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;

public class MailSend {
    private final String emailSend;
    private final String passwordSend;

    //create cons
    public MailSend(String email, String password) {
        this.emailSend = email;
        this.passwordSend = password;
    }

    //send mail
    public void sendMail(String toMail, String subject, String body) throws MessagingException {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.host", "smtp.gmail.com");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(emailSend, passwordSend);
            }
        });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(emailSend));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toMail));
        message.setSubject(subject);
        message.setText(body);

        Transport.send(message);
    }
}


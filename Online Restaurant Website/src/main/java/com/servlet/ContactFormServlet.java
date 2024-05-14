package com.servlet;

import java.io.IOException;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/submit-contact-form")
public class ContactFormServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Enable detailed mail session logging
        System.setProperty("mail.debug", "true");

        // Ensure TLS 1.2 is enabled, especially for older Java versions
        System.setProperty("https.protocols", "TLSv1.2");

        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String message = request.getParameter("message");
        
        try {
            sendEmail(name, email, message);
            response.sendRedirect("thank-you.html");
        } catch (MessagingException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "There was an error sending your message.");
        }
    }
    
    private void sendEmail(String name, String email, String message) throws MessagingException {
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");
        
        // Explicitly trust the SMTP server. Consider narrowing this in a production environment.
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");

        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
                return new javax.mail.PasswordAuthentication("maskeraj105@gmail.com", "tancentraj");
            }
        });

        session.setDebug(true); // Enable session debugging

        Message emailMessage = new MimeMessage(session);
        emailMessage.setFrom(new InternetAddress("maskeraj105@gmail.com"));
        emailMessage.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
        emailMessage.setSubject("Contact Form Submission from " + name);
        emailMessage.setText("You have received a new message from your website contact form.\n\nHere are the details:\nName: " + name + "\nEmail: " + email + "\nMessage:\n" + message);
        
        Transport.send(emailMessage);
    }
}

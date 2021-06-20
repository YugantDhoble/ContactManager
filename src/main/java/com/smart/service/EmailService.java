package com.smart.service;

import java.util.Properties;

import javax.mail.*;
import javax.mail.internet.*;

import org.springframework.stereotype.Service;

@Service
public class EmailService {
	public boolean sendEmail(String subject,String message,String to) {
		boolean f=false;
		String host="smtp.gmail.com";
		String from="varificationyugant@gmail.com";
		Properties properties=System.getProperties();
		System.out.println("Properties"+ properties);
		
		properties.put("mail.smtp.host", host);
		properties.put("mail.smtp.port", "465");
		properties.put("mail.smtp.ssl.enable", "true");
		properties.put("mail.smtp.auth", "true");

		Session session=Session.getInstance(properties,new Authenticator() {
			
		@Override
		protected PasswordAuthentication getPasswordAuthentication() {
			return new PasswordAuthentication("varificationyugant@gmail.com", "07152246540");

		}

		
			
		});
		
		session.setDebug(true);
		
		MimeMessage m=new MimeMessage(session);


		try {
			m.setFrom(from);
			m.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
			m.setSubject(subject);     
			m.setText(message);
			m.setContent(message,"text/html");
			Transport.send(m);
			System.out.println("Send Success..............!");
			f=true;
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return f;
		
	}

}

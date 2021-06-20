/*package com.smart.config;

import java.util.Random;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.service.EmailService;

@Controller	
public class ForgotController {

	Random random=new Random(1000);
	
	@Autowired
	private EmailService emailService;

	
	@RequestMapping("/forgot")
	public String openEmailForm() {
		
		return "forgot_email_form";
		
	}
	
	
	@PostMapping("/send-otp")
	public String sentOTP(@RequestParam("email") String email,HttpSession session) {
		int otp=random.nextInt(99999);

		String subject="OTP from SCM";
		String message="<h1> OTP ="+otp+"</h1>";
		String to=email;
		boolean flag=this.emailService.sendEmail(subject, message, to);
		
		
		if(flag) {
			return "change_password";
		}else {
			session.setAttribute(message, "Check your email id");
		}
		
		return "varify_otp";
	}
}*/

 package com.smart.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import com.razorpay.*;

import com.smart.dao.ContactRepository;
import com.smart.dao.UserRepository;
import com.smart.entities.Contact;
import com.smart.entities.User;
import com.smart.helper.Message;

import netscape.javascript.JSObject;

@Controller
@RequestMapping("/user")
public class UserController {
	
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ContactRepository contactRepository;
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@ModelAttribute
	public void addCommonData(Model model,Principal principal) {
		String userName=principal.getName();
		User user=userRepository.getUserByUserName(userName);
		model.addAttribute("user", user);
	}
	
	
	@RequestMapping("/index")
	public String dashboard(Model model,Principal principal) {
		return "normal/user_dashboard";
	}
	
	@GetMapping("/add-contact")
	public String openAddContactForm(Model model) {
		
		model.addAttribute("title", "Add Contact");
		model.addAttribute("contact", new Contact());
		return "normal/add-contact-form";
	}
	
	@PostMapping("/process-contact")
	public String processContact(@ModelAttribute Contact contact,@RequestParam("profileImage") MultipartFile file,Principal principal,HttpSession session) {
		try {
		String name=principal.getName();
		User user=this.userRepository.getUserByUserName(name);
		
		if(file.isEmpty()) {
			contact.setImage("contact.png");
			
		}else {  
			contact.setImage(file.getOriginalFilename());
			File saveFile=new ClassPathResource("static/images").getFile();
 			Path path=Paths.get(saveFile.getAbsolutePath()+File.separator+file.getOriginalFilename());
			
			Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
		}
		
		
		
		contact.setUser(user);
		
		user.getContacts().add(contact); 
		this.userRepository.save(user);
			
			
			session.setAttribute("message", new Message("Your contact has been successfully added!!","success"));
			
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			session.setAttribute("message", new Message("Something went wrong Try again!!","danger"));

		}
		
		
		return "normal/add-contact-form";
		
		
	}
	
	@GetMapping("/show-contacts/{page}")
	public String showContacts(@PathVariable("page") Integer page, Model m,Principal principal) {
		m.addAttribute("title","Show user Contacts" );
		
		String userName=principal.getName();
		User user=this.userRepository.getUserByUserName(userName);
		Pageable pageable=PageRequest.of(page,5);
		Page<Contact> contacts= this.contactRepository.findContactByUser(user.getId(),pageable);
		
		m.addAttribute("contacts", contacts);
		m.addAttribute("currentPage", page);
		m.addAttribute("totalPages", contacts.getTotalPages());
		
		return "normal/show_contacts";
	}
	
	@GetMapping("/{cId}/contact")
	public String showContactDetail(@PathVariable("cId") Integer cId,Model model,Principal principal) {
		
		Optional<Contact> contactOptional=this.contactRepository.findById(cId);
		Contact contact = contactOptional.get();
		
		String userName=principal.getName();
		User user=this.userRepository.getUserByUserName(userName);
		
		if(user.getId()==contact.getUser().getId()) {
		
		model.addAttribute("contact", contact);
		model.addAttribute("title", contact.getName());
		}
		return "normal/contact_detail";
	}
	
	
	@GetMapping("/delete/{cid}")
	public String deleteContact(@PathVariable("cid") Integer cId,Model model,HttpSession session) {
		Optional<Contact> contactOptional=this.contactRepository.findById(cId);
		Contact contact=contactOptional.get();
		
		contact.setUser(null);
		
		this.contactRepository.delete(contact);
		
		session.setAttribute("message", new Message("Contact deleted successfully... ","success"));
		return "redirect:/user/show-contacts/0";
	}
	
	
	@PostMapping("/update-contact/{cid}")
	public String updateForm(@PathVariable("cid") Integer cid ,Model m) {
		
		m.addAttribute("title", "Update Contact");
		
		Contact contact=this.contactRepository.findById(cid).get();
		
		m.addAttribute("contact", contact);
		return "normal/update_form";
	}
	
	//update contact handeller
	@PostMapping("/process-update")
    public String updateHandler(@ModelAttribute Contact contact,@RequestParam("profileImage") MultipartFile file,Model m,HttpSession session,Principal principal ) {
    	
		try {
			//old contact details
			
			Contact oldcontactDetail=this.contactRepository.findById(contact.getcId()).get();
			
			if(!file.isEmpty()) {
				//delete old image
				
				File deleteFile=new ClassPathResource("static/images").getFile();
                File file1=new File(deleteFile,oldcontactDetail.getImage());
                file1.delete();
				
				
				//update new image
				
				
				File saveFile=new ClassPathResource("static/images").getFile();
	 			Path path=Paths.get(saveFile.getAbsolutePath()+File.separator+file.getOriginalFilename());
				
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				contact.setImage(file.getOriginalFilename());
			
				
				
				
			}else {
				contact.setImage(oldcontactDetail.getImage());
			}
			
			User user=this.userRepository.getUserByUserName(principal.getName());
			contact.setUser(user);
			
			this.contactRepository.save(contact);
			
			session.setAttribute("message",new Message("Your contact has been updated..!!","success"));
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return "redirect:/user/"+contact.getcId()+"/contact";
    }
	
	
	//your profile Handeler
	
	@GetMapping("/profile")
	public String yourProfile(Model model) {
		
		model.addAttribute("title", "Profile page");
		return "normal/profile";
	}
	
	//open Setting handeller
	@GetMapping("/settings")
	public  String openSetting() {
		return "normal/settings";
	}
	
	//Change password ... handler
	@PostMapping("/change-password")
	public String changePassword(@RequestParam("oldPassword") String oldPassword,@RequestParam("newPassword") String newPassword,Principal principal,HttpSession session) {
		String userName=principal.getName();
		User currentUser=this.userRepository.getUserByUserName(userName);
		
		if(this.bCryptPasswordEncoder.matches(oldPassword, currentUser.getPassword())) {
			currentUser.setPassword(this.bCryptPasswordEncoder.encode(newPassword));
			this.userRepository.save(currentUser);
			session.setAttribute("message", new Message("Your password has successfully changed","success"));
			
		}else {
			session.setAttribute("message", new Message("Wrong old password","danger"));
			return "redirect:/user/settings/";


		}
		
		
		return "redirect:/user/index/";
	}
	
	@PostMapping("/create_order")
	@ResponseBody
	public String createOrder(@RequestBody Map<String,Object> data) throws RazorpayException {
		System.out.println(data);
		int amt=Integer.parseInt(data.get("amount").toString());
		var client=new RazorpayClient("rzp_test_qe1X5poeDFu4p5", "wO2wF0fGV4dwfEgMSWT3dyBk");
		JSONObject ob=new JSONObject();
		ob.put("amount", amt*100);
	    ob.put("currency", "INR");
	    ob.put("receipt", "txn_235425");
	    
	    Order order=client.Orders.create(ob);
	    System.out.println(order);
		
		return order.toString();
	}

}

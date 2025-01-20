package com.smart.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.smart.dao.ContactRepo;
import com.smart.dao.UserRepository;
import com.smart.entity.Contact;
import com.smart.entity.User;
import com.smart.helper.Message;

@Controller
@RequestMapping("/user")
public class UserController {
	
	@Autowired
	private UserRepository userRepo;
	
	@Autowired
	private ContactRepo contactRepo;
	
	@ModelAttribute
	public void addCommonData(Model model, Principal principal) {
		String name = principal.getName();
		User user = userRepo.getUserByUsername(name);
		
		model.addAttribute("user", user);;
		
		System.out.println("NAME : "+name);
	}
	
	
	//dashbord home
	@GetMapping("/index")
	public String dashboard(Model model, Principal principal) {
		model.addAttribute("title", "User Dashboard");
		
		return "normal/user_dashboard";
	}
	
	// add open add form handler
	@GetMapping("/add_contact")
	public String openAddContactForm(Model model) {
		model.addAttribute("title", "Add Contact");
		model.addAttribute("contact", new Contact());
		
		
		return "normal/add_contact_form";
	}
	
	//add contact
	@PostMapping("/process-contact")
	public String processContact(@ModelAttribute Contact contact,Principal principal,
			                     @RequestParam("profileImage") MultipartFile file,HttpSession session){
		System.out.println("DATA: -  "+contact);
		String name = principal.getName(); 
		
		
		
		try {
			
			User user = userRepo.getUserByUsername(name);
			
			//process and upload file
			if(file.isEmpty()) {
				//write message
				contact.setImage("contact.png");
			}else {
				contact.setImage(file.getOriginalFilename());
				File saveFile = new ClassPathResource("static/img").getFile();
				
				Path path = Paths.get(saveFile.getAbsolutePath(), File.separator+file.getOriginalFilename());
				
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				System.out.println("Image is uploded.");
			}
			
			contact.setUser(user);
			
			user.getContacts().add(contact);
			User save = userRepo.save(user);
			System.out.println("Added To DB : "+save);
			
			//message success
			session.setAttribute("message", new Message("Youe contact is added !! Add more.","success"));
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			session.setAttribute("message", new Message("Somthis is wrong  !! Try again.","danger"));
			
			e.printStackTrace();
		}
		return "normal/add_contact_form";
		
	}
	
	//show contact handler
	@GetMapping("/show_contacts/{page}")
	public String showContact(@PathVariable("page") Integer page ,Model model,Principal principal) {
		model.addAttribute("title", "Show Contact");
		
		//get list
		String userName = principal.getName();
		User user = this.userRepo.getUserByUsername(userName);
		 
		PageRequest pagebble = PageRequest.of(page, 5);
		
		Page<Contact> contacts = contactRepo.findContactByUser(user.getId(),pagebble);
		model.addAttribute("contacts", contacts);
		model.addAttribute("currentPage", page);
		model.addAttribute("totlePages", contacts.getTotalPages());
		
		return "normal/show_contacts";
	}
	
	//show particular contact details
	@GetMapping("/{cId}/contact")
	  public String showContactDetails(@PathVariable("cId") Integer cId,Model model,Principal principal) {
		
		System.out.println("CID: "+cId);
		Optional<Contact> contactOptional = contactRepo.findById(cId);
		Contact contact = contactOptional.get();
		
		String userName = principal.getName();
		User user = userRepo.getUserByUsername(userName);
		
		if(user.getId()==contact.getUser().getId())  {
			model.addAttribute("contact", contact);
		    model.addAttribute("title", contact.getName());
		}
		  
		  return "normal/contact_details";
	  }

	@GetMapping("/delete/{cId}")
	public String deleteContact(@PathVariable("cId") Integer cId, Model model,HttpSession session) {
		Contact contact = contactRepo.findById(cId).get();
		
		contact.setUser(null);
		
		contactRepo.delete(contact);
		
		session.setAttribute("message", new Message("Contact deleted sucessfully...!!", "success"));
		
		return "redirect:/user/show_contacts/0";
		
	}
	
	//update form
	@PostMapping("/update_contact/{cId}")
	public String updateForm(Model model,@PathVariable("cId") Integer cId) {
		 model.addAttribute("title", "Update Contact");
		 
		 Contact contact = contactRepo.findById(cId).get();
		 model.addAttribute("contact", contact);
		
		return "normal/update_form";
		
	}
	
	//update by id
	@PostMapping("/process-update")
	public String updateHandler(@ModelAttribute Contact contact,Model model,
			@RequestParam("profileImage") MultipartFile file, HttpSession session,Principal principal) {
		
		try {
			Contact oldContact = contactRepo.findById(contact.getcId()).get();
			
			
			if(!file.isEmpty()){
				File deleteFile = new ClassPathResource("static/img").getFile();
				File file2 = new File(deleteFile,oldContact.getImage());
				file2.delete();
				
                File saveFile = new ClassPathResource("static/img").getFile();
				
				Path path = Paths.get(saveFile.getAbsolutePath(), File.separator+file.getOriginalFilename());
				
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				contact.setImage(file.getOriginalFilename());
				
			}else {
//				contact.setImage(oldContact.getImage());
			}
			User user = userRepo.getUserByUsername(principal.getName());
			contact.setUser(user);
			contactRepo.save(contact);
			session.setAttribute("message", new Message("Contact Updated sucessfully...!!", "success"));
			
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
		
		System.out.println("Contact : "+contact);
		
		return "redirect:/user/"+contact.getcId()+"/contact";
	}
	
	//your profile
	@GetMapping("/profile")
	public String yourProfile(Model model) {
		model.addAttribute("title", "Profile Page");
		return "normal/profile";
	}
	
}





















package com.smart.controller;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.dao.UserRepository;
import com.smart.entity.User;
import com.smart.helper.Message;

@Controller
public class HomeController {
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder; 
	
	@Autowired
	private UserRepository userRepo;
	
	@GetMapping("/")
 	public String test(Model model) {
		
		model.addAttribute("title", "Smart Contact");
	
		return "home";
	}
	
	@GetMapping("/about")
 	public String about(Model model) {
		
		model.addAttribute("title", "About- Smart Contact");
	
		return "about";
	}
	
	@GetMapping("/signup")
 	public String signup(Model model) {
		
		model.addAttribute("title", "Signup- Smart Contact");
		model.addAttribute("user", new User());
	
		return "signup";
	}
	
	@PostMapping("/do_register")
	public String register(@Valid @ModelAttribute User user, BindingResult result1 ,@RequestParam(value="agreement",defaultValue = "false") boolean agreement,
			     Model model, HttpSession session) {
		
		try {
			if(!agreement) {
				System.out.println("You are not agreement.");
				 throw new Exception("You are not agreement.");
			}
			
			if(result1.hasErrors()) {
				System.err.println("Result : "+result1);
				model.addAttribute("user", user);
				return "signup";
			}
			
			user.setRole("ROLE_USER");
			user.setEnabled(true);
			user.setImageUrl("default.png");
			user.setPassword(passwordEncoder.encode(user.getPassword()));
			
			User save = userRepo.save(user);
			model.addAttribute("user", new User());
			session.setAttribute("message", new Message("Sucessfully Registerd", "alert-success"));
			return "signup";
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			model.addAttribute("user", user);
			session.setAttribute("message", new Message("Something went wrong....!!"+e.getMessage(), "alert-danger"));
			return "signup";
		}
 	}
	@GetMapping("/signin")
	public String customLogin(Model model) {
		model.addAttribute("title", "Login Page");
		return "login";
	}

}

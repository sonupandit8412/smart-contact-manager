package com.smart.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.smart.dao.ContactRepo;
import com.smart.dao.UserRepository;
import com.smart.entity.Contact;
import com.smart.entity.User;

@RestController
public class SearchController {
	
	@Autowired
	private UserRepository userRepo;
	
	@Autowired
	private ContactRepo contactRepo;

	@GetMapping("/search/{query}")
	public ResponseEntity<?> search(@PathVariable("query") String query,Principal principal){
		System.out.println(query);
		
		User user = userRepo.getUserByUsername(principal.getName());
		
		List<Contact> contacts = contactRepo.findByNameContainingAndUser(query, user);
		return ResponseEntity.ok(contacts);
	}
}

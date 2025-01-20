package com.smart.dao;

import java.awt.print.Pageable;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.smart.entity.Contact;
import com.smart.entity.User;
@Repository
public interface ContactRepo extends JpaRepository<Contact, Integer>{
	
	@Query("from Contact as d where d.user.id=:userId") 
	public Page<Contact> findContactByUser(@Param("userId") int userId, PageRequest pagebble);
	
	public List<Contact> findByNameContainingAndUser(String name,User user);

}

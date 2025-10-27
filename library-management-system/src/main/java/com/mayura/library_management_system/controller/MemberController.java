package com.mayura.library_management_system.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mayura.library_management_system.model.Author;
import com.mayura.library_management_system.model.Member;
import com.mayura.library_management_system.repository.AuthorRepository;
import com.mayura.library_management_system.repository.MemberRepository;

@RestController
@RequestMapping("/api/member")
@CrossOrigin(origins = "http://localhost:5173")
public class MemberController {
	
	@Autowired
	MemberRepository memberRepo;
	
	@GetMapping
	public List<Member> getAllMember() {
	    return memberRepo.findAll();
	}
	
	
	 @DeleteMapping("/{id}")
	 public void deleteMember(@PathVariable Long id) {
		 memberRepo.deleteById(id);
	 }

	 @PostMapping
	    public Member addMember(@RequestBody Member member) {
	        return memberRepo.save(member);
	    }

	

}

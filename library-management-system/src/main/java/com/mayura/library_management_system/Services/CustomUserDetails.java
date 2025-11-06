package com.mayura.library_management_system.Services;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.mayura.library_management_system.model.User;
import com.mayura.library_management_system.repository.UserRepository;

@Service
public class CustomUserDetails implements UserDetailsService{

	private UserRepository userRepo;
	public CustomUserDetails(UserRepository userRepo) {
		this.userRepo = userRepo;
	}
	  
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		// TODO Auto-generated method stub
		
		User user = userRepo.findByUsername(username);
		if(user == null) {
			throw new UsernameNotFoundException("User not found with given username");
		}
		return org.springframework.security.core.userdetails.User.withUsername(user.getUsername()).password(user.getPassword()).roles(user.getRole()).build();
	}
	

}

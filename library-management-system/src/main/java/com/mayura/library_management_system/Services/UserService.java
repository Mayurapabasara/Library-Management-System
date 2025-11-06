package com.mayura.library_management_system.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.mayura.library_management_system.model.User;
import com.mayura.library_management_system.repository.UserRepository;

@Service
public class UserService {

	private final UserRepository userRepo;
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	//constructor
	public UserService(UserRepository userRepo) {
		this.userRepo = userRepo;
	}
	
	public void registerUser(String username, String password) {
		User user = new User();
		user.setUsername(username);
		user.setPassword(passwordEncoder.encode(password));
		user.setRole("USER");
		userRepo.save(user);
	}
}

package com.mayura.library_management_system.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mayura.library_management_system.model.Author;
import com.mayura.library_management_system.model.Book;

public interface BookRepository extends JpaRepository<Book, Long>{
	
	

}

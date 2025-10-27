package com.mayura.library_management_system.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mayura.library_management_system.model.Author;

public interface AuthorRepository extends JpaRepository<Author, Long>{
	Optional<Author> findByName(String name);
}

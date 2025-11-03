package com.mayura.SimpleWebApp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.mayura.SimpleWebApp.model.Product;

public interface ProductRepository extends JpaRepository<Product, Integer> {

}

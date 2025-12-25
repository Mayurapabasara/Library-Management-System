package com.mayura.library_management_system.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.mayura.library_management_system.model.Rating;

public interface RatingRepository extends JpaRepository<Rating, Long> {
	
	List<Rating> findByItemId(Long itemId);
}

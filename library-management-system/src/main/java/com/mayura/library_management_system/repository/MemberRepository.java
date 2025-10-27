package com.mayura.library_management_system.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mayura.library_management_system.model.Member;

public interface MemberRepository extends JpaRepository<Member, Long>{

}

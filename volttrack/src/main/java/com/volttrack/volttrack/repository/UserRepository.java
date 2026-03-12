package com.volttrack.volttrack.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.volttrack.volttrack.entity.User;

public interface UserRepository  extends JpaRepository<User,Long>{

    
}

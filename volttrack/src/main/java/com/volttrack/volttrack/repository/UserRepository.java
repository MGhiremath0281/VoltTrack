package com.volttrack.volttrack.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.volttrack.volttrack.entity.User;

@Repository
public interface UserRepository  extends JpaRepository<User,Long>{

    
}

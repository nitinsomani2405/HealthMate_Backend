package com.project.HealthMate.Repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.HealthMate.Models.user;


@Repository
public interface UserRepository extends JpaRepository<user, UUID>{

	Optional<user> findByEmail(String email);
		
}

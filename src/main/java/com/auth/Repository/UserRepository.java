package com.auth.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.auth.Models.user;


@Repository
public interface UserRepository extends JpaRepository<user, UUID>{

	Optional<user> findByEmail(String email);

	@Query("SELECT u FROM user u WHERE u.role = 'doctor'")
	List<user> findAllDoctors();
}

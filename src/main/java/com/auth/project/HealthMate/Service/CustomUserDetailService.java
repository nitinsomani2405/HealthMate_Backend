package com.auth.project.HealthMate.Service;

import java.util.Optional;
import java.util.UUID;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.auth.project.HealthMate.Models.UserPrinciple;
import com.auth.project.HealthMate.Models.user;
import com.auth.project.HealthMate.Repository.UserRepository;

@Service
public class CustomUserDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<user> user = userRepository.findByEmail(email);
        
        if (user.isEmpty()) {
            throw new UsernameNotFoundException("User not found with email: " + email);
        }

        return UserPrinciple.build(user.get());
    }
    public UserDetails loadUserById(UUID userId) throws UsernameNotFoundException {
        // New method to load user by userId
        return userRepository.findById(userId)
                .map(UserPrinciple::build)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with ID: " + userId));
    }
}

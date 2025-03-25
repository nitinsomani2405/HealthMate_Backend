package com.project.HealthMate.Config;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

import com.project.HealthMate.Service.CustomUserDetailService;
import com.project.HealthMate.Service.TokenService;  // Your token service


public class TokenFilter extends OncePerRequestFilter {

    private final TokenService tokenService;
    private final CustomUserDetailService customUserDetailService;

    public TokenFilter(TokenService tokenService,CustomUserDetailService customUserDetailService) {
        this.tokenService = tokenService;
        this.customUserDetailService=customUserDetailService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Extract the access token from the request
        String accessToken = extractAccessTokenFromRequest(request);

//        // Extract the userId from the request (assuming it's a String in the request)
//        String userIdString = extractUserIdFromRequest(request);
//        System.out.println(userIdString);
//        UUID userId = null;
//        
//        try {
//            if (userIdString != null && !userIdString.isEmpty()) {
//                userId = UUID.fromString(userIdString);  // Convert the String to UUID
//                System.out.println(userId);
//            }
//        } catch (IllegalArgumentException e) {
//            // Handle invalid UUID format if necessary
//        	System.out.println(e);
//            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid userId format.");
//            return;
//        }

        // Validate the token and userId
        UUID userId = tokenService.getUserIdFromAccessToken(accessToken); 
        if (accessToken != null && tokenService.validateAccessToken(accessToken)) {
            // If the token is valid and belongs to the correct user, load user details
            var userDetails=customUserDetailService.loadUserById(userId);// Assuming userId can be used to load user
            // Create an authentication object and set it in the SecurityContext
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // Proceed with the filter chain
        filterChain.doFilter(request, response);
    }

    // Helper method to extract the access token from the request
    private String extractAccessTokenFromRequest(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);  // Extract the access token from the header
        }
        return null;
    }

//    // Helper method to extract the userId from the request (e.g., header or session)
//    private String extractUserIdFromRequest(HttpServletRequest request) {
//        // Extract the userId from the request (modify this based on your design)
//    	System.out.println("request");
//        String userId = request.getHeader("UserId");  // Example: Extract from header
//        if (userId != null) {
//            return userId;  // Return the userId as a string
//        }
//        return "";  // Default if no userId is provided, handle as needed
//    }
}

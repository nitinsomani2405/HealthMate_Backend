package com.auth.Config;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.auth.Service.CustomUserDetailService;
import com.auth.Service.TokenService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;


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

        String path = request.getRequestURI();
        // Extract the access token from the request
        String accessToken = extractAccessTokenFromRequest(request);
        if(accessToken == null) {
            filterChain.doFilter(request, response);
            return;
        }

        if (path.equals("/api/login") || path.equals("/api/signup")||path.equals("/api/status")||path.equals("/")) {
            filterChain.doFilter(request, response); // skip token validation
            return;
        }


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


}

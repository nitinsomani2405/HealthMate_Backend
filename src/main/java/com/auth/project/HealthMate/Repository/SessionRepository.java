package com.auth.project.HealthMate.Repository;

import java.util.UUID;

import com.auth.project.HealthMate.Models.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.auth.project.HealthMate.Models.*;

@Repository
public interface SessionRepository extends JpaRepository<Session, UUID> {
    Session findByAccessToken(String accessToken);
    Session findByRefreshToken(String refreshToken);
    @Query(value = "SELECT s.user_id FROM sessions s WHERE s.access_token = :accessToken", nativeQuery = true)
    UUID findUserIdByAccessToken(@Param("accessToken") String accessToken);

}

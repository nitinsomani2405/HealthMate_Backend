package com.profile.Repository;

import com.profile.Models.Doctor;
import com.profile.Models.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, UUID> {
    Optional<Doctor> findByDoctorId(UUID userId);

    @Query("SELECT d FROM Doctor d WHERE d.user.id IN :userIds")
    List<Doctor> findByUserIds(List<UUID> userIds);
}

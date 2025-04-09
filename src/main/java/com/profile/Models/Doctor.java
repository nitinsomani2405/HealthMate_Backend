package com.profile.Models;


import com.auth.Models.user;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "doctor")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Doctor {

    @Id
    @Column(name = "doctor_id")
    private UUID doctorId;


    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "doctor_id", referencedColumnName = "id", insertable = false, updatable = false)
    private user user;

    @Column(nullable = true)
    private String dateOfBirth;

    @Column(nullable = true)
    private String speciality;

    @Column(nullable = true)
    private String clinicName;

    @Column(nullable = false)
    private boolean profileCompleted ;
}


package com.profile.Models;

import com.auth.Models.user;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "patient")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Patient {

    @Id
    @Column(name = "patient_id")
    private UUID patientId;


    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_id", referencedColumnName = "id", insertable = false, updatable = false)
    private user user;

    @Column(nullable = true)
    private String dateOfBirth;

    @Column(nullable = false)
    private boolean profileCompleted ;
}

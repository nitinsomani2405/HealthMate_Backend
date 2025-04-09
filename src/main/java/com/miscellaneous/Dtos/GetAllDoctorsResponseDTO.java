package com.miscellaneous.Dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


//dto to provide all doctors to patient
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetAllDoctorsResponseDTO {
    private List<DoctorResponseDTO> doctors;
}
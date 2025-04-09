package com.miscellaneous.Dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetConnectedPatientsResponseDTO {
    private List<ConnectedPatientDTO> patients;
}
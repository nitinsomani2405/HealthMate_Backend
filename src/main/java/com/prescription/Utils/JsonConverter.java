package com.prescription.Utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.prescription.Dtos.PrescriptionMedicineDTO;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Arrays;
import java.util.List;

@Converter
public class JsonConverter implements AttributeConverter<List<PrescriptionMedicineDTO>, String> {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<PrescriptionMedicineDTO> attribute) {
        try {
            String json = objectMapper.writeValueAsString(attribute);
            System.out.println("🚀 Serialized medicines JSON: " + json); // 🐞 Debug here
            return json;
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Could not convert list to JSON string", e);
        }
    }

    @Override
    public List<PrescriptionMedicineDTO> convertToEntityAttribute(String dbData) {
        try {
            return Arrays.asList(objectMapper.readValue(dbData, PrescriptionMedicineDTO[].class));
        } catch (Exception e) {
            throw new RuntimeException("Could not convert JSON string to list", e);
        }
    }
}

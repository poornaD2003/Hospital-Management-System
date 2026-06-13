package com.example.HospitalManagmentSystem.DTO;

import lombok.Data;

@Data
public class patientDTO {
    private Long id;
    private String name;
    private String email;
    private Integer age;
    private String phone;
}
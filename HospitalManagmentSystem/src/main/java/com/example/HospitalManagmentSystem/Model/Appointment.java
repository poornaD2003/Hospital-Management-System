package com.example.HospitalManagmentSystem.Model;



import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "appointments")

@AllArgsConstructor
@NoArgsConstructor
@Data

public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;

    @Column(name = "patient_id", nullable = false)
    private Long patientId; // String name එක වෙනුවට Long ID එකක් දැම්මා

    @Transient // මේකෙන් කියන්නේ මේ field එක ඩේටාබේස් එකේ සේව් වෙන්න එපා කියලා
    private String patientName;

    @Column(name = "doctor_name", nullable = false)
    private String doctorName;

    @Column(name = "specialization", nullable = false)
    private  String specialization;
    @Column(name = "appointment_date", nullable = false)
    private String appointmentDate;

    @Column(name = "appointment_time", nullable = false)
    private String appointmentTime;
}

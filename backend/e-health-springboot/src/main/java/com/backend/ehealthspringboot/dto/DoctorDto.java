package com.backend.ehealthspringboot.dto;

import com.backend.ehealthspringboot.domain.Doctor;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class DoctorDto {
    Doctor doctor;
    MultipartFile profileImage;
    String currentDoctorUsername;

}

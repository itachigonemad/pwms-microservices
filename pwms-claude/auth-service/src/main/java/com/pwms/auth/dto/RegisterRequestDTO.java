package com.pwms.auth.dto;

import com.pwms.auth.model.User.Role;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequestDTO {
    private String  username;
    private String  password;
    private Role    role;           // ADMIN or PATIENT
    private Integer referenceId;    // patientId or adminId from their service
}

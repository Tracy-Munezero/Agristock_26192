package auca.ac.rw.AgriStock1.model.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PasswordResetRequest {
    private String email;
    private String otpCode;
    private String newPassword;
}
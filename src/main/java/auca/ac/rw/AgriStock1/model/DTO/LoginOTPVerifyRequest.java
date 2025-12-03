package auca.ac.rw.AgriStock1.model.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginOTPVerifyRequest {
    private String email;
    private String otpCode;
}
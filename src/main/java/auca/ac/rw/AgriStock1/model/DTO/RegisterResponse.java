package auca.ac.rw.AgriStock1.model.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RegisterResponse {
    private String message;
    private Long userId;
    private String email;
    private String role;
}
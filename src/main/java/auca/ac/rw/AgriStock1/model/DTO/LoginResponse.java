package auca.ac.rw.AgriStock1.model.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponse {
    private String message;
    private String accessToken;
    private String refreshToken;
    private Long userId;
    private String username;
    private String email;
    private String role;
    private Long farmerId;
    private Long buyerId;
}
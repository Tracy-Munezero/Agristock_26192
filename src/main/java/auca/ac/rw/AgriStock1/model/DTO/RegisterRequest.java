package auca.ac.rw.AgriStock1.model.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    private String email;
    private String username;
    private String password;
    private String role; // FARMER or BUYER
    private String firstName;
    private String lastName;
    private String phone;
    private String businessName; // For buyers only
    private Long villageId;
}


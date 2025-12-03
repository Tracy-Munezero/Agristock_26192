package auca.ac.rw.AgriStock1.model.DTO;

import auca.ac.rw.AgriStock1.model.AccountStatus;

import auca.ac.rw.AgriStock1.model.Farmer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountRequest {
    private String username;
    private String password;
    private AccountStatus accountStatus;
    private Long farmerId;
}

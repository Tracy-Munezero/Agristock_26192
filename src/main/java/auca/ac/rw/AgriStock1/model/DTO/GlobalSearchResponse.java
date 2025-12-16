package auca.ac.rw.AgriStock1.model.DTO;

import auca.ac.rw.AgriStock1.model.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GlobalSearchResponse {
    private List<Farmer> farmers;
    private List<Buyer> buyers;
    private List<Product> products;
    private List<Transaction> transactions;
    private List<User> users;
    private Integer totalResults;
}
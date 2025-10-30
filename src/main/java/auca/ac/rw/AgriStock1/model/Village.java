package auca.ac.rw.AgriStock1.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "villages")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Village {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long villageId;

    @Column(unique = true, nullable = false)
    @NotBlank(message = "Village code is required")
    private String villageCode;

    @Column(nullable = false)
    @NotBlank(message = "Village name is required")
    private String villageName;

    @ManyToOne
    @JoinColumn(name = "cell_id", nullable = false)
    private Cell cell;
}

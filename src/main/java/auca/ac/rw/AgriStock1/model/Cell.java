package auca.ac.rw.AgriStock1.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "cells")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cell {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cellId;

    @Column(unique = true, nullable = false)
    @NotBlank(message = "Cell code is required")
    private String cellCode;

    @Column(nullable = false)
    @NotBlank(message = "Cell name is required")
    private String cellName;

    @ManyToOne
    @JoinColumn(name = "sector_id", nullable = false)
    private Sector sector;

    @OneToMany(mappedBy = "cell", cascade = CascadeType.ALL)
    @JsonIgnore  // Don't show sectors list
    private List<Village> villages;
}

package com.hospitalApi.surgery.models;

import java.time.LocalDate;
import java.util.List;

import com.hospitalApi.consults.models.Consult;
import com.hospitalApi.shared.models.Auditor;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Surgery extends Auditor {

    @ManyToOne
    @JoinColumn(nullable = false)
    private Consult consult;

    @ManyToOne
    @JoinColumn(nullable = false)
    private SurgeryType surgeryType;

    @Column(nullable = false)
    private Double hospitalCost;

    @Column(nullable = false)
    private Double surgeryCost;

    @Column(nullable = true)
    private LocalDate performedDate;

    @OneToMany(mappedBy = "surgery", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SurgeryEmployee> surgeryEmployees;

    public Surgery(Consult consult, SurgeryType surgeryType, Double hospitalCost, Double surgeryCost) {
        this.consult = consult;
        this.surgeryType = surgeryType;
        this.hospitalCost = hospitalCost;
        this.surgeryCost = surgeryCost;
    }

}

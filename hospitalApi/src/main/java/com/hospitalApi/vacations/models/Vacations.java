package com.hospitalApi.vacations.models;

import com.hospitalApi.shared.models.Auditor;

import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class Vacations extends Auditor {

}
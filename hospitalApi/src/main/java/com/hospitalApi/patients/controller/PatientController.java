package com.hospitalApi.patients.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hospitalApi.patients.mappers.PatientMapper;
import com.hospitalApi.patients.services.PatientService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/v1/patients")
@RequiredArgsConstructor
public class PatientController {
    private final PatientService patientService;
    private final PatientMapper patientMapper;
}

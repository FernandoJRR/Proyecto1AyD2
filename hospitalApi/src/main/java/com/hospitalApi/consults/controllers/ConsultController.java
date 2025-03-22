package com.hospitalApi.consults.controllers;

import org.springframework.web.bind.annotation.RestController;

import com.hospitalApi.consults.mappers.ConsultMapper;
import com.hospitalApi.consults.port.ForConsultPort;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("api/v1/consults")
@RequiredArgsConstructor
public class ConsultController {

    private final ForConsultPort consultPort;
    private final ConsultMapper consultMapper;
}

package com.hospitalApi.medicines.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hospitalApi.medicines.dtos.MedicineResponseDTO;
import com.hospitalApi.medicines.dtos.UpdateMedicineRequestDTO;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("v1/medicines")
@RequiredArgsConstructor
public class MedicineController {

    @GetMapping("/all")
    public ResponseEntity<List<MedicineResponseDTO>> getAllMedicines() {
        return ResponseEntity.ok().body(new ArrayList<MedicineResponseDTO>());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MedicineResponseDTO> getMedicine(@RequestParam String param) {
        return ResponseEntity.ok().body(new MedicineResponseDTO());
    }

    @PostMapping()
    public ResponseEntity<MedicineResponseDTO> postMethodName() {
        return ResponseEntity.ok().body(new MedicineResponseDTO());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<MedicineResponseDTO> patchMethodName(
            @RequestParam Long id,
            @RequestBody @Valid UpdateMedicineRequestDTO dto) {
        return ResponseEntity.ok().body(new MedicineResponseDTO());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> deleteMethodName(@RequestParam Long id) {
        return ResponseEntity.ok().body(true);
    }

}

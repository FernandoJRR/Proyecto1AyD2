package com.hospitalApi.rooms.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hospitalApi.auth.login.dtos.LoginRequestDTO;
import com.hospitalApi.auth.login.dtos.LoginResponseDTO;
import com.hospitalApi.shared.exceptions.DuplicatedEntryException;
import com.hospitalApi.shared.exceptions.NotFoundException;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/rooms")
public class RoomController {

    @PostMapping
    public ResponseEntity<LoginResponseDTO> createRoom(
            @RequestBody @Valid LoginRequestDTO request) throws DuplicatedEntryException, NotFoundException {
        return null;
    }

    @PatchMapping
    public ResponseEntity<LoginResponseDTO> updateRoom(
            @RequestBody @Valid LoginRequestDTO request) throws DuplicatedEntryException, NotFoundException {
        return null;
    }

    @GetMapping("/{roomId}")
    public ResponseEntity<LoginResponseDTO> findRoomById(
            @PathVariable String roomId,
            @RequestBody @Valid LoginRequestDTO request) throws DuplicatedEntryException, NotFoundException {
        return null;
    }

    @GetMapping("/all")
    public ResponseEntity<LoginResponseDTO> findAllRooms(
            @RequestBody @Valid LoginRequestDTO request) throws DuplicatedEntryException, NotFoundException {
        return null;
    }
}

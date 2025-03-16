package com.hospitalApi.users.mappers;

import org.mapstruct.Mapper;

import com.hospitalApi.users.dtos.CreateUserRequestDTO;
import com.hospitalApi.users.models.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    public User fromCreateUserRequestDtoToUser(CreateUserRequestDTO dto);
}

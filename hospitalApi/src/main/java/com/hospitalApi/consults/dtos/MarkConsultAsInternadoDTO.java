package com.hospitalApi.consults.dtos;

import lombok.Data;

@Data
public class MarkConsultAsInternadoDTO {
    private String consultId;
    private String roomId;
}

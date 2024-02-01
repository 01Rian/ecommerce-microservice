package com.ecommerce.userapi.domain.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserDto {

    private long id;
    private String nome;
    private String cpf;
    private String endereco;
    private String email;
    private String telefone;
    private LocalDateTime dataCadastro;
}

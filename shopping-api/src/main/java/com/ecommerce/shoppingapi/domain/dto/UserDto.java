package com.ecommerce.shoppingapi.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto {

    @NotBlank
    private String nome;
    @NotBlank
    private String cpf;
    @NotBlank
    private String endereco;
    @NotBlank
    private String email;
    @NotBlank
    private String telefone;
    @NotNull
    private LocalDate dataCadastro;
}

package com.ecommerce.shoppingapi.domain.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto {

    @NotBlank
    private String name;
    @NotBlank
    private String cpf;
    @NotBlank
    private String address;
    @NotBlank
    private String email;
    @NotBlank
    private String phone;
    @NotNull
    private LocalDateTime dateRegister;
}

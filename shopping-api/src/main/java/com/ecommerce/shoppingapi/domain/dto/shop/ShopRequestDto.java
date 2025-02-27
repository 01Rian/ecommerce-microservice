package com.ecommerce.shoppingapi.domain.dto.shop;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShopRequestDto {

    @NotNull(message = "O identificador do usuário não pode ser nulo")
    @NotBlank(message = "O identificador do usuário não pode estar em branco")
    private String userIdentifier;
    
    @NotNull(message = "A lista de items não pode ser nula")
    private List<ItemDto> items;
}
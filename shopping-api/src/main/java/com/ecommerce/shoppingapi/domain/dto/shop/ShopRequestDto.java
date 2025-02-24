package com.ecommerce.shoppingapi.domain.dto.shop;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class ShopRequestDto extends ShopDto {

    @NotBlank(message = "O identificador do usuário não pode estar em branco")
    @Override
    public String getUserIdentifier() {
        return super.getUserIdentifier();
    }

    @NotNull(message = "A lista de items não pode ser nula")
    @Override
    public List<ItemDto> getItems() {
        return super.getItems();
    }
}
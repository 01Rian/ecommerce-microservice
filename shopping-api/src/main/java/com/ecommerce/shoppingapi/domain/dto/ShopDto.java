package com.ecommerce.shoppingapi.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShopDto {

    @NotBlank
    private String userIdentifier;

    private Float total;
    private LocalDateTime date;

    @NotNull
    private List<ItemDto> items;
}

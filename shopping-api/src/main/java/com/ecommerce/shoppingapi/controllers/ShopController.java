package com.ecommerce.shoppingapi.controllers;

import com.ecommerce.shoppingapi.domain.dto.ShopDto;
import com.ecommerce.shoppingapi.services.ShopService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ShopController {

    @Autowired
    private ShopService shopService;

    @GetMapping("/shoppings")
    public List<ShopDto> getShops() {
        return shopService.getAll();
    }

    @GetMapping("/shoppings/shopByUser/{userIdentifier}")
    public List<ShopDto> getShops(@PathVariable("userIdentifier") String userIdentifier) {
        return shopService.getByUser(userIdentifier);
    }

    @GetMapping("/shoppings/shopByDate")
    public List<ShopDto> getShops(@RequestBody ShopDto dto) {
        return shopService.getByDate(dto);
    }

    @GetMapping("/shoppings/{id}")
    public ShopDto findById(@PathVariable("id") Long id) {
        return shopService.findById(id);
    }

    @PostMapping("/shoppings")
    @ResponseStatus(HttpStatus.CREATED)
    public ShopDto newShop(@Valid @RequestBody ShopDto dto) {
        return shopService.save(dto);
    }
}

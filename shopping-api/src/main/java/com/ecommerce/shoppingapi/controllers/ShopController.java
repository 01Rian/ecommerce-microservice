package com.ecommerce.shoppingapi.controllers;

import com.ecommerce.shoppingapi.domain.dto.ShopDto;
import com.ecommerce.shoppingapi.domain.dto.ShopReportDto;
import com.ecommerce.shoppingapi.services.ShopService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
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

    @GetMapping("/shoppings/search")
    public List<ShopDto> getShopsByFilter
            (
            @RequestParam(name = "startDate", required = true)
            @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate startDate,
            @RequestParam(name = "endDate", required = false)
            @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate endDate,
            @RequestParam(name = "maxValue", required = false) Float maxValue
            ) {
        return shopService.getShopsByFilter(startDate, endDate, maxValue);
    }

    @GetMapping("/shoppings/report")
    public ShopReportDto getReportByDate
            (
            @RequestParam(name = "startDate", required = true)
            @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate startDate,
            @RequestParam(name = "endDate", required = true)
            @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate endDate
            ) {
        return shopService.getReportByDate(startDate, endDate);
    }

    @PostMapping("/shoppings")
    @ResponseStatus(HttpStatus.CREATED)
    public ShopDto newShop(@Valid @RequestBody ShopDto dto) {
        return shopService.save(dto);
    }
}

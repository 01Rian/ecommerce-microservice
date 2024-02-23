package com.ecommerce.shoppingapi.controllers;

import com.ecommerce.shoppingapi.domain.dto.ShopDto;
import com.ecommerce.shoppingapi.domain.dto.ShopReportDto;
import com.ecommerce.shoppingapi.services.ShopService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/shoppings")
public class ShopController {

    private final ShopService shopService;

    @GetMapping
    public List<ShopDto> getAllShops() {
        return shopService.getAll();
    }

    @GetMapping("/shopByUser/{userIdentifier}")
    public List<ShopDto> getShopsByUserIdentifier(@PathVariable("userIdentifier") String userIdentifier) {
        return shopService.getByUser(userIdentifier);
    }

    @GetMapping("/{id}")
    public ShopDto findById(@PathVariable("id") Long id) {
        return shopService.findById(id);
    }

    @GetMapping("/search")
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

    @GetMapping("/report")
    public ShopReportDto getReportByDate
            (
            @RequestParam(name = "startDate", required = true)
            @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate startDate,
            @RequestParam(name = "endDate", required = true)
            @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate endDate
            ) {
        return shopService.getReportByDate(startDate, endDate);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ShopDto newShop(@Valid @RequestBody ShopDto dto) {
        return shopService.save(dto);
    }
}

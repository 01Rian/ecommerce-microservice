package com.ecommerce.shoppingapi.controllers;

import com.ecommerce.shoppingapi.domain.dto.report.ShopReportResponseDto;
import com.ecommerce.shoppingapi.domain.dto.shop.ShopRequestDto;
import com.ecommerce.shoppingapi.domain.dto.shop.ShopResponseDto;
import com.ecommerce.shoppingapi.services.ShopService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/shoppings")
public class ShopController {

    private final ShopService shopService;

    @GetMapping
    public List<ShopResponseDto> getAllShops() {
        return shopService.getAll();
    }

    @GetMapping("/pageable")
    public Page<ShopResponseDto> getAllShopsPage(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "linesPerPage", defaultValue = "12") Integer linesPerPage,
            @RequestParam(value = "direction", defaultValue = "ASC") String direction,
            @RequestParam(value = "orderBy", defaultValue = "total") String orderBy
    ) {
        PageRequest pageRequest = PageRequest.of(page, linesPerPage, Sort.Direction.valueOf(direction), orderBy);

        return shopService.getAllPage(pageRequest);
    }

    @GetMapping("/shopByUser/{userIdentifier}")
    public List<ShopResponseDto> getShopsByUserIdentifier(@PathVariable("userIdentifier") String userIdentifier) {
        return shopService.getByUser(userIdentifier);
    }

    @GetMapping("/{id}")
    public ShopResponseDto findById(@PathVariable("id") Long id) {
        return shopService.findById(id);
    }

    @GetMapping("/search")
    public List<ShopResponseDto> getShopsByFilter(
            @RequestParam(name = "startDate", required = true)
            @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate startDate,
            @RequestParam(name = "endDate", required = false)
            @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate endDate,
            @RequestParam(name = "maxValue", required = false) BigDecimal maxValue
            ) {
        return shopService.getShopsByFilter(startDate, endDate, maxValue);
    }

    @GetMapping("/report")
    public ShopReportResponseDto getReportByDate(
            @RequestParam(name = "startDate", required = true)
            @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate startDate,
            @RequestParam(name = "endDate", required = true)
            @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate endDate
            ) {
                ShopReportResponseDto report = shopService.getReportByDate(startDate, endDate);

        if (report.getCount() != 0) {
            DecimalFormat df = new DecimalFormat("#.##");
            String formattedMean = df.format(report.getMean()).replace(",", ".");
            report.setMean(new BigDecimal(formattedMean));
        } else {
            report.setMean(BigDecimal.ZERO);
            report.setTotal(BigDecimal.ZERO);
        }

        return report;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ShopResponseDto newShop(@Valid @RequestBody ShopRequestDto dto) {
        return shopService.save(dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteShop(@PathVariable("id") Long id) {
        shopService.delete(id);
    }
}

package com.ecommerce.shoppingapi.controllers;

import com.ecommerce.shoppingapi.domain.dto.ShopDto;
import com.ecommerce.shoppingapi.domain.dto.ShopReportDto;
import com.ecommerce.shoppingapi.services.ShopService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.text.DecimalFormat;
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

    @GetMapping("/pageable")
    public Page<ShopDto> getAllShopsPage(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "linesPerPage", defaultValue = "12") Integer linesPerPage,
            @RequestParam(value = "direction", defaultValue = "ASC") String direction,
            @RequestParam(value = "orderBy", defaultValue = "total") String orderBy
    ) {
        PageRequest pageRequest = PageRequest.of(page, linesPerPage, Sort.Direction.valueOf(direction), orderBy);

        return shopService.getAllPage(pageRequest);
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
    public List<ShopDto> getShopsByFilter(
            @RequestParam(name = "startDate", required = true)
            @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate startDate,
            @RequestParam(name = "endDate", required = false)
            @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate endDate,
            @RequestParam(name = "maxValue", required = false) Float maxValue
            ) {
        return shopService.getShopsByFilter(startDate, endDate, maxValue);
    }

    @GetMapping("/report")
    public ShopReportDto getReportByDate(
            @RequestParam(name = "startDate", required = true)
            @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate startDate,
            @RequestParam(name = "endDate", required = true)
            @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate endDate
            ) {
        ShopReportDto report = shopService.getReportByDate(startDate, endDate);

        if (report.getCount() != 0) {
            DecimalFormat df = new DecimalFormat("#.##");
            String formattedMean = df.format(report.getMean()).replace(",", ".");
            report.setMean(Double.valueOf(formattedMean));
        } else {
            report.setMean(0.0);
            report.setTotal(0.0);
        }

        return report;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ShopDto newShop(@Valid @RequestBody ShopDto dto) {
        return shopService.save(dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteShop(@PathVariable("id") Long id) {
        shopService.delete(id);
    }
}

package com.ecommerce.shoppingapi.services;

import com.ecommerce.shoppingapi.domain.dto.ItemDto;
import com.ecommerce.shoppingapi.domain.dto.ShopDto;
import com.ecommerce.shoppingapi.domain.dto.ShopReportDto;
import com.ecommerce.shoppingapi.domain.entities.ShopEntity;
import com.ecommerce.shoppingapi.mappers.impl.ShopMapper;
import com.ecommerce.shoppingapi.repositories.ShopRepository;
import com.ecommerce.shoppingapi.repositories.impl.ReportRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ShopService {

    @Autowired
    private ShopRepository shopRepository;

    @Autowired
    private ReportRepositoryImpl reportRepository;

    @Autowired
    private ShopMapper mapper;

    public List<ShopDto> getAll() {
        List<ShopEntity> shops = shopRepository.findAll();
        return shops
                .stream()
                .map(mapper::mapTo)
                .collect(Collectors.toList());
    }

    public List<ShopDto> getByUser(String userIdentifier) {
        List<ShopEntity> shops = shopRepository.findAllByUserIdentifier(userIdentifier);
        return shops
                .stream()
                .map(mapper::mapTo)
                .collect(Collectors.toList());
    }

    public List<ShopDto> getByDate(ShopDto shopDto) {
        List<ShopEntity> shops = shopRepository.findAllByDateGreaterThan(shopDto.getDate());
        return shops
                .stream()
                .map(mapper::mapTo)
                .collect(Collectors.toList());
    }

    public ShopDto findById(Long id) {
        Optional<ShopEntity> shop = shopRepository.findById(id);
        return shop.map(shopEntity -> mapper.mapTo(shopEntity)).orElse(null);
    }

    public ShopDto save(ShopDto shopDto) {
        shopDto.setTotal(
                shopDto.getItems()
                        .stream()
                        .map(ItemDto::getPrice)
                        .reduce((float) 0, Float::sum)
        );

        ShopEntity shopEntity = mapper.mapFrom(shopDto);
        shopEntity.setDate(LocalDateTime.now());
        shopEntity = shopRepository.save(shopEntity);
        return mapper.mapTo(shopEntity);
    }

    public List<ShopDto> getShopsByFilter(LocalDate startDate, LocalDate endDate, Float maxValue) {
        List<ShopEntity> shops = reportRepository.getShopByFilters(startDate, endDate, maxValue);
        return shops
                .stream()
                .map(mapper::mapTo)
                .collect(Collectors.toList());
    }

    public ShopReportDto getReportByDate(LocalDate startDate, LocalDate endDate) {
        return reportRepository.getReportByDate(startDate, endDate);
    }
 }

package com.ecommerce.shoppingapi.services;

import com.ecommerce.shoppingapi.domain.dto.product.ProductResponseDto;
import com.ecommerce.shoppingapi.domain.dto.report.ShopReportResponseDto;
import com.ecommerce.shoppingapi.domain.dto.shop.ItemDto;
import com.ecommerce.shoppingapi.domain.dto.shop.ShopRequestDto;
import com.ecommerce.shoppingapi.domain.dto.shop.ShopResponseDto;
import com.ecommerce.shoppingapi.domain.entities.Shop;
import com.ecommerce.shoppingapi.exception.ShoppingNotFoundException;
import com.ecommerce.shoppingapi.mappers.impl.ShopMapper;
import com.ecommerce.shoppingapi.repositories.ShopRepository;
import com.ecommerce.shoppingapi.repositories.impl.ReportRepositoryImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShopService {

    private final ShopRepository shopRepository;
    private final ReportRepositoryImpl reportRepository;
    private final ShopMapper mapper;
    private final ProductService productService;
    private final UserService userService;

    @Transactional(readOnly = true)
    public List<ShopResponseDto> getAll() {
        List<Shop> shops = shopRepository.findAll();
        return shops
                .stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<ShopResponseDto> getAllPage(PageRequest page) {
        Page<Shop> shops = shopRepository.findAll(page);
        return shops.map(mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public List<ShopResponseDto> getByUser(String userIdentifier) {
        List<Shop> shops = shopRepository.findAllByUserIdentifier(userIdentifier);
        return shops
                .stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ShopResponseDto findById(Long id) {
        Shop shop = shopRepository.findById(id).orElseThrow(() -> new ShoppingNotFoundException("id", id));
        return mapper.toResponse(shop);
    }

    @Transactional(readOnly = true)
    public List<ShopResponseDto> getShopsByFilter(LocalDate startDate, LocalDate endDate, BigDecimal maxValue) {
        List<Shop> shops = reportRepository.getShopByFilters(startDate, endDate, maxValue);
        return shops
                .stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ShopReportResponseDto getReportByDate(LocalDate startDate, LocalDate endDate) {
        return reportRepository.getReportByDate(startDate, endDate);
    }

    @Transactional
    public ShopResponseDto save(ShopRequestDto shopDto) {
        if (userService.getUserByCfp(shopDto.getUserIdentifier()) == null) {
            return null;
        }

        if (!validateProducts(shopDto.getItems())) {
            return null;
        }

        Shop shop = mapper.fromRequest(shopDto);
        shop.setDate(LocalDateTime.now());
        shop.setTotal(shopDto.getItems()
                        .stream()
                        .map(ItemDto::getPrice)
                        .reduce(BigDecimal.ZERO, BigDecimal::add));

        shopRepository.save(shop);
        return mapper.toResponse(shop);
    }

    private boolean validateProducts(List<ItemDto> items) {
        for (ItemDto item : items) {
            ProductResponseDto productDto = productService.getProductByIdentifier(item.getProductIdentifier());

            if (productDto == null) {
                return false;
            }
            item.setPrice(productDto.getPrice());
        }
        return true;
    }

    @Transactional
    public void delete(Long id) throws ShoppingNotFoundException {
        boolean exist = shopRepository.existsById(id);
        if (!exist) {
            throw new ShoppingNotFoundException("id", id);	
        }
        shopRepository.deleteById(id);
    }
 }

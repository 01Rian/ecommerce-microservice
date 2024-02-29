package com.ecommerce.shoppingapi.services;

import com.ecommerce.shoppingapi.domain.dto.*;
import com.ecommerce.shoppingapi.domain.dto.product.ProductDto;
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
    public List<ShopDto> getAll() {
        List<Shop> shops = shopRepository.findAll();
        return shops
                .stream()
                .map(mapper::mapTo)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<ShopDto> getAllPage(PageRequest page) {
        Page<Shop> shops = shopRepository.findAll(page);
        return shops.map(mapper::mapTo);
    }

    @Transactional(readOnly = true)
    public List<ShopDto> getByUser(String userIdentifier) {
        List<Shop> shops = shopRepository.findAllByUserIdentifier(userIdentifier);
        return shops
                .stream()
                .map(mapper::mapTo)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ShopDto findById(Long id) {
        Shop shop = shopRepository.findById(id).orElseThrow(ShoppingNotFoundException::new);
        return mapper.mapTo(shop);
    }

    @Transactional(readOnly = true)
    public List<ShopDto> getShopsByFilter(LocalDate startDate, LocalDate endDate, Float maxValue) {
        List<Shop> shops = reportRepository.getShopByFilters(startDate, endDate, maxValue);
        return shops
                .stream()
                .map(mapper::mapTo)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ShopReportDto getReportByDate(LocalDate startDate, LocalDate endDate) {
        return reportRepository.getReportByDate(startDate, endDate);
    }

    @Transactional
    public ShopDto save(ShopDto shopDto) {
        if (userService.getUserByCfp(shopDto.getUserIdentifier()) == null) {
            return null;
        }

        if (!validateProducts(shopDto.getItems())) {
            return null;
        }

        shopDto.setTotal(
                shopDto.getItems()
                        .stream()
                        .map(ItemDto::getPrice)
                        .reduce((float) 0, Float::sum)
        );

        Shop shop = mapper.mapFrom(shopDto);
        shop.setDate(LocalDateTime.now());
        shopRepository.save(shop);

        return mapper.mapTo(shop);
    }

    private boolean validateProducts(List<ItemDto> items) {
        for (ItemDto item : items) {
            ProductDto productDto = productService.getProductByIdentifier(item.getProductIdentifier());

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
            throw new ShoppingNotFoundException();
        }
        shopRepository.deleteById(id);
    }
 }

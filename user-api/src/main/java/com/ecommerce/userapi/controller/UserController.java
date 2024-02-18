package com.ecommerce.userapi.controller;

import com.ecommerce.userapi.domain.dto.UserDto;
import com.ecommerce.userapi.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/users")
    public List<UserDto> getUsers() {
        return userService.getAll();
    }

    @GetMapping("/users/pageable")
    public Page<UserDto> getUsersPage(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "linesPerPage", defaultValue = "12") Integer linesPerPage,
            @RequestParam(value = "direction", defaultValue = "ASC") String direction,
            @RequestParam(value = "orderBy", defaultValue = "nome") String orderBy
    ) {
        PageRequest pageRequest = PageRequest.of(page, linesPerPage, Sort.Direction.valueOf(direction), orderBy);

        return userService.getAllPage(pageRequest);
    }

    @GetMapping("/users/{id}")
    public UserDto findById(@PathVariable("id") Long id) {
        return userService.findById(id);
    }

    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto newUser(@Valid @RequestBody UserDto userDto) {
        return userService.save(userDto);
    }

    @GetMapping("/users/cpf/{cpf}")
    public UserDto findByCpf(@PathVariable("cpf") String cpf) {
        return userService.findByCpf(cpf);
    }

    @DeleteMapping("/users/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long id) {
         userService.delete(id);
    }

    @GetMapping("/users/search")
    public List<UserDto> queryByName(
            @RequestParam(name = "name", required = true)
            String name) {
        return userService.queryByName(name);
    }
}

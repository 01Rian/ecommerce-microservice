package com.ecommerce.userapi.controller;

import com.ecommerce.userapi.domain.dto.UserDto;
import com.ecommerce.userapi.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class UserController {

    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/user/")
    public List<UserDto> getUsers() {
        return null;
    }

    @GetMapping("/user/{id}")
    public UserDto findById(@PathVariable("id") Long id) {
        return null;
    }

    @PostMapping("/user")
    public UserDto newUser(@RequestBody UserDto userDto) {
        return null;
    }

    @GetMapping("/user/cpf/{cpf}")
    public UserDto findByCpf(@PathVariable("cpf") String cpf) {
        return null;
    }

    @DeleteMapping("/user/{id}")
    public UserDto delete(@PathVariable("id") Long id) {
        return null;
    }

    @GetMapping("/user/search")
    public List<UserDto> queryByName(
            @RequestParam(name = "nome", required = true)
            String nome) {
        return null;
    }
}

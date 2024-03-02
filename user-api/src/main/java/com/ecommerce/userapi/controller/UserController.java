package com.ecommerce.userapi.controller;

import com.ecommerce.userapi.domain.dto.UserDto;
import com.ecommerce.userapi.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @GetMapping
    public List<UserDto> getUsers() {
        return userService.getAll();
    }

    @GetMapping("/pageable")
    public Page<UserDto> getUsersPage(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "linesPerPage", defaultValue = "12") Integer linesPerPage,
            @RequestParam(value = "direction", defaultValue = "ASC") String direction,
            @RequestParam(value = "orderBy", defaultValue = "name") String orderBy
    ) {
        PageRequest pageRequest = PageRequest.of(page, linesPerPage, Sort.Direction.valueOf(direction), orderBy);

        return userService.getAllPage(pageRequest);
    }

    @GetMapping("/{id}")
    public UserDto findById(@PathVariable("id") Long id) {
        return userService.findById(id);
    }

    @GetMapping("/cpf/{cpf}")
    public UserDto findByCpf(@PathVariable("cpf") String cpf) {
        return userService.findByCpf(cpf);
    }

    @GetMapping("/search")
    public List<UserDto> queryByName(
            @RequestParam(name = "name", required = true)
            String name) {
        return userService.queryByName(name);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto newUser(@Valid @RequestBody UserDto userDto) {
        return userService.save(userDto);
    }

    @PutMapping("/{cpf}")
    public UserDto updateUser(@RequestBody UserDto userDto, @PathVariable("cpf") String cpf) {
        return userService.update(userDto, cpf);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long id) {
         userService.delete(id);
    }
}

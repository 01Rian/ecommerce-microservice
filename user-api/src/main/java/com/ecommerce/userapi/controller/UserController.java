package com.ecommerce.userapi.controller;

import com.ecommerce.userapi.domain.dto.UserDto;
import com.ecommerce.userapi.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<UserDto>> findAll() {
        return ResponseEntity.ok(userService.findAll());
    }

    @GetMapping("/pageable")
    public ResponseEntity<Page<UserDto>> findByPage(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "linesPerPage", defaultValue = "12") Integer linesPerPage,
            @RequestParam(value = "direction", defaultValue = "ASC") String direction,
            @RequestParam(value = "orderBy", defaultValue = "name") String orderBy
    ) {
        PageRequest pageRequest = PageRequest.of(page, linesPerPage, Sort.Direction.valueOf(direction), orderBy);
        return ResponseEntity.ok(userService.findByPage(pageRequest));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> findById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(userService.findById(id));
    }

    @GetMapping("/cpf/{cpf}")
    public ResponseEntity<UserDto> findByCpf(@PathVariable("cpf") String cpf) {
        return ResponseEntity.ok(userService.findByCpf(cpf));
    }

    @GetMapping("/search")
    public ResponseEntity<List<UserDto>> findByQueryName(
            @RequestParam(name = "name", required = true)
            String name) {
        return ResponseEntity.ok(userService.findByQueryName(name));
    }

    @PostMapping
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody UserDto userDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userService.save(userDto));
    }

    @PutMapping("/{cpf}")
    public ResponseEntity<UserDto> updateUser(@RequestBody UserDto userDto, @PathVariable("cpf") String cpf) {
        return ResponseEntity.ok(userService.update(userDto, cpf));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") Long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

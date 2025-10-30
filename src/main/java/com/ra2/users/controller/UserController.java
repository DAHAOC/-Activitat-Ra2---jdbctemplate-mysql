package com.ra2.users.controller;

import com.ra2.users.model.User;
import com.ra2.users.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users") // Ruta base
public class UserController {
    @Autowired
    private UserRepository repository;


    // CREATE
    @PostMapping
    public ResponseEntity<String> createUser(@RequestBody User user) {
        repository.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body("Usuari creat correctament");
    }

    // READ ALL
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = repository.findAll();
        return ResponseEntity.status(HttpStatus.OK).body(users.isEmpty() ? null : users);
    }

    // READ BY ID
    @GetMapping("/{user_id}")
    public ResponseEntity<User> getUserById(@PathVariable Long user_id) {
        User user = repository.findById(user_id);
        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

    // UPDATE COMPLET
    @PutMapping("/{user_id}")
    public ResponseEntity<String> updateUser(@PathVariable Long user_id, @RequestBody User user) {
        user.setId(user_id);
        repository.update(user);
        return ResponseEntity.status(HttpStatus.OK).body("Usuari actualitzat correctament");
    }

    // UPDATE PARCIAL (name)
    @PatchMapping("/{user_id}/name")
    public ResponseEntity<String> updateUserName(@PathVariable Long user_id, @RequestParam String name) {
        repository.updateName(user_id, name);
        return ResponseEntity.status(HttpStatus.OK).body("Nom actualitzat correctament");
    }

    // DELETE
    @DeleteMapping("/{user_id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long user_id) {
        repository.delete(user_id);
        return ResponseEntity.status(HttpStatus.OK).body("Usuari eliminat correctament");
    }
}

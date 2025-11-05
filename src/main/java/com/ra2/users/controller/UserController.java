package com.ra2.users.controller;

import com.ra2.users.model.User;
import com.ra2.users.service.UserService;

import org.apache.catalina.connector.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/users") // Ruta base
public class UserController {
    private UserService service;
    public UserController(UserService service) {
        this.service = service;
    }

    // CREATE: crea un nou usuari a la base de dades
    @PostMapping
    public ResponseEntity<String> createUser(@RequestBody User user) {
        
        service.saveUser(user);// crida el repositori per guardar l'usuari
        return ResponseEntity.status(HttpStatus.CREATED).body("Usuari creat correctament");
    }

    // READ ALL: retorna tots els usuaris
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = service.getAllUsers(); // consulta tots els usuaris
        return ResponseEntity.status(HttpStatus.OK).body(users.isEmpty() ? null : users);
    }

    // READ BY ID: retorna un usuari concret per id
    @GetMapping("/{user_id}")
    public ResponseEntity<User> getUserById(@PathVariable Long user_id) {
        User user = service.getUserById(user_id); // consulta l'usuari per id
        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

    // UPDATE COMPLET: actualitza tots els camps d'un usuari
    @PutMapping("/{user_id}")
    public ResponseEntity<String> updateUser(@PathVariable Long user_id, @RequestBody User user) {
        user.setId(user_id); // assegura que l'id coincideixi amb el path
        service.updateUser(user); // crida el repositori per actualitzar
        return ResponseEntity.status(HttpStatus.OK).body("Usuari actualitzat correctament");
    }

    // UPDATE PARCIAL: actualitza només el nom de l'usuari
    @PatchMapping("/{user_id}/name")
    public ResponseEntity<String> updateUserName(@PathVariable Long user_id, @RequestParam String name) {
        service.updateUserName(user_id, name); // actualitza només el nom
        return ResponseEntity.status(HttpStatus.OK).body("Nom actualitzat correctament");
    }

    // DELETE: elimina un usuari per id
    @DeleteMapping("/{user_id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long user_id) {
        service.deleteUser(user_id); // crida el repositori per eliminar l'usuari
        return ResponseEntity.status(HttpStatus.OK).body("Usuari eliminat correctament");
    }

    @PostMapping("/{user_id}/uploadImage")
    public String uploadUserImage(@PathVariable Long user_id, @RequestParam MultipartFile image) {
        return service.uploadImage(user_id, image);
        
    }   




}

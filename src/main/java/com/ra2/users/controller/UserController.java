package com.ra2.users.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.ra2.users.logging.CustomLogging;
import com.ra2.users.model.User;
import com.ra2.users.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService service;
    private final CustomLogging customLogging;

    public UserController(UserService service, CustomLogging customLogging) {
        this.service = service;
        this.customLogging = customLogging;
    }

    @PostMapping
    public ResponseEntity<String> createUser(@RequestBody User user) {
        try {
            service.saveUser(user);
            customLogging.logInfo("UserController", "createUser", "Usuari creat: " + user.getEmail());
            return ResponseEntity.status(HttpStatus.CREATED).body("Usuari creat correctament");
        } catch (Exception e) {
            customLogging.logError("UserController", "createUser", "Error creant usuari: " + user.getEmail(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creant usuari");
        }
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        try {
            List<User> users = service.getAllUsers();
            customLogging.logInfo("UserController", "getAllUsers", "Peticio GET /api/users");
            return ResponseEntity.status(HttpStatus.OK).body(users.isEmpty() ? null : users);
        } catch (Exception e) {
            customLogging.logError("UserController", "getAllUsers", "Error recuperant tots els usuaris", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/{user_id}")
    public ResponseEntity<User> getUserById(@PathVariable Long user_id) {
        try {
            User user = service.getUserById(user_id);
            customLogging.logInfo("UserController", "getUserById", "Peticio GET /api/users/" + user_id);
            return ResponseEntity.status(HttpStatus.OK).body(user);
        } catch (Exception e) {
            customLogging.logError("UserController", "getUserById", "Error recuperant usuari ID " + user_id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("/{user_id}")
    public ResponseEntity<String> updateUser(@PathVariable Long user_id, @RequestBody User user) {
        try {
            user.setId(user_id);
            service.updateUser(user);
            customLogging.logInfo("UserController", "updateUser", "Usuari actualitzat ID: " + user_id);
            return ResponseEntity.status(HttpStatus.OK).body("Usuari actualitzat correctament");
        } catch (Exception e) {
            customLogging.logError("UserController", "updateUser", "Error actualitzant usuari ID " + user_id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error actualitzant usuari");
        }
    }

    @PatchMapping("/{user_id}/name")
    public ResponseEntity<String> updateUserName(@PathVariable Long user_id, @RequestParam String name) {
        try {
            service.updateUserName(user_id, name);
            customLogging.logInfo("UserController", "updateUserName", "Nom actualitzat usuari ID " + user_id + " a " + name);
            return ResponseEntity.status(HttpStatus.OK).body("Nom actualitzat correctament");
        } catch (Exception e) {
            customLogging.logError("UserController", "updateUserName", "Error actualitzant nom usuari ID " + user_id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error actualitzant nom");
        }
    }

    @DeleteMapping("/{user_id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long user_id) {
        try {
            service.deleteUser(user_id);
            customLogging.logInfo("UserController", "deleteUser", "Usuari eliminat ID " + user_id);
            return ResponseEntity.status(HttpStatus.OK).body("Usuari eliminat correctament");
        } catch (Exception e) {
            customLogging.logError("UserController", "deleteUser", "Error eliminant usuari ID " + user_id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error eliminant usuari");
        }
    }

    @PostMapping("/{user_id}/image")
    public ResponseEntity<String> uploadUserImage(
            @PathVariable Long user_id,
            @RequestParam("imageFile") MultipartFile imageFile) {
        try {
            String imageUrl = service.uploadUserImage(user_id, imageFile);
            if (imageUrl == null) {
                customLogging.logInfo("UserController", "uploadUserImage", "Usuari no trobat ID " + user_id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuari no trobat");
            }
            customLogging.logInfo("UserController", "uploadUserImage", "Imatge pujada correctament per ID " + user_id);
            return ResponseEntity.status(HttpStatus.OK).body("Imatge pujada correctament: " + imageUrl);
        } catch (IOException e) {
            customLogging.logError("UserController", "uploadUserImage", "Error pujant imatge per ID " + user_id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al desar la imatge");
        }
    }

    @PostMapping("/upload-csv")
    public ResponseEntity<String> uploadUsersCSV(@RequestParam("csvFile") MultipartFile csvFile) {
        try {
            int count = service.processCSV(csvFile);
            customLogging.logInfo("UserController", "uploadUsersCSV", "CSV processat: " + csvFile.getOriginalFilename() + ", afegits: " + count);
            return ResponseEntity.ok("S'han afegit " + count + " registres.");
        } catch (Exception e) {
            customLogging.logError("UserController", "uploadUsersCSV", "Error processant CSV: " + csvFile.getOriginalFilename(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error en processar el CSV: " + e.getMessage());
        }
    }
}

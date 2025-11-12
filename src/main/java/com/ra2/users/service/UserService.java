package com.ra2.users.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.ra2.users.model.User;
import com.ra2.users.repository.UserRepository;

@Service
public class UserService {
    private final UserRepository repository;

    // RUTA base per desar fitxers

    public UserService(UserRepository repository) {
        this.repository = repository;

    }

    public User getUserById(Long id) {
        return repository.findById(id);
    }

    public List<User> getAllUsers() {
        return repository.findAll();
    }

    public void saveUser(User user) {
        repository.save(user);
    }

    public void updateUser(User user) {
        repository.update(user);
    }

    public void deleteUser(Long id) {
        repository.delete(id);
    }

    public void updateUserName(Long id, String name) {
        repository.updateName(id, name);
    }

    public String uploadUserImage(Long userId, MultipartFile imageFile) throws IOException {
        User user = repository.findById(userId);
        if (user == null) {
            return null; // usuari no trobat
        }

        // Carpeta on guardarem la imatge
        String folderPath = "src/main/resources/public/images";
        Files.createDirectories(Paths.get(folderPath));

        // Nom del fitxer: user_1_timestamp.jpg
        String fileName = "user_" + userId + "_" + System.currentTimeMillis() + ".jpg";
        Path imagePath = Paths.get(folderPath, fileName);

        // Desa la imatge al disc
        Files.write(imagePath, imageFile.getBytes(), StandardOpenOption.CREATE);

        // Desa la ruta a la base de dades
        String dbPath = "/images/" + fileName;
        repository.updateImagePath(userId, dbPath);

        return dbPath;
    }

}

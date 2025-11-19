package com.ra2.users.service;

import java.io.BufferedReader;

import java.io.InputStreamReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.ra2.users.model.User;
import com.ra2.users.repository.UserRepository;

@Service
public class UserService {
    private final UserRepository repository;
    private final String UPLOAD_CSV_DIR = "src/main/resources/public/csv_processed";
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

    // Pujar csv
    public int processCSV(MultipartFile csvFile) throws Exception {
        List<User> users = new ArrayList<>();
        int lineNumber = 0;

        // Leer contenido del CSV
        try (BufferedReader br = new BufferedReader(new InputStreamReader(csvFile.getInputStream()))) {
            String line;
            while ((line = br.readLine()) != null) {
                lineNumber++;

                // Saltar la cabecera
                if (lineNumber == 1)
                    continue;

                String[] fields = line.split(",");

                // Validar que tenga al menos 3 campos: name, email, password
                if (fields.length < 3) {
                    System.err.println(
                            "Línea " + lineNumber + " ignorada, menos de 3 campos: " + Arrays.toString(fields));
                    continue;
                }

                User user = new User();
                user.setName(fields[0].trim());
                user.setEmail(fields[1].trim());
                user.setPassword(fields[2].trim());

                // Description opcional
                user.setDescription(fields.length > 3 ? fields[3].trim() : "Sense descripcio");

                users.add(user);
            }
        }

        // Guardar en base de datos
        int count = 0;
        for (User user : users) {
            try {
                repository.save(user);
                count++;
            } catch (Exception e) {
                System.err.println("Error insertando usuario " + user.getEmail() + ": " + e.getMessage());
            }
        }

        // Guardar el archivo CSV en carpeta csv_processed
        String folderPath = "src/main/resources/public/csv_processed"; // carpeta a nivel de proyecto
        Path directoryPath = Paths.get(folderPath);
        if (Files.notExists(directoryPath)) {
            Files.createDirectories(directoryPath);
        }

        Path filePath = Paths.get(folderPath, csvFile.getOriginalFilename());
        Files.copy(csvFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        System.out.println("CSV procesado correctamente. Registros agregados: " + count);
        return count;
    }

}

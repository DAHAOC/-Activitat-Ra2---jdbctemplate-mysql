package com.ra2.users.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.ra2.users.logging.CustomLogging;
import com.ra2.users.model.User;
import com.ra2.users.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository repository;
    private final CustomLogging customLogging;

    private static final String IMAGE_DIR = "src/main/resources/public/images";
    private static final String CSV_DIR = "src/main/resources/public/csv_processed";

    public UserService(UserRepository repository, CustomLogging customLogging) {
        this.repository = repository;
        this.customLogging = customLogging;
    }

    public User getUserById(Long id) {
        try {
            User user = repository.findById(id);
            customLogging.logInfo("UserService", "getUserById", "Usuari recuperat: " + (user != null ? user.getEmail() : "no trobat"));
            return user;
        } catch (Exception e) {
            customLogging.logError("UserService", "getUserById", "Error recuperant usuari amb ID " + id, e);
            return null;
        }
    }

    public List<User> getAllUsers() {
        try {
            List<User> users = repository.findAll();
            customLogging.logInfo("UserService", "getAllUsers", "Total usuaris recuperats: " + users.size());
            return users;
        } catch (Exception e) {
            customLogging.logError("UserService", "getAllUsers", "Error recuperant tots els usuaris", e);
            return new ArrayList<>();
        }
    }

    public void saveUser(User user) {
        LocalDateTime now = LocalDateTime.now();
        if (user.getDataCreated() == null) user.setDataCreated(now);
        if (user.getDataUpdated() == null) user.setDataUpdated(now);

        try {
            repository.save(user);
            customLogging.logInfo("UserService", "saveUser", "Usuari creat correctament: " + user.getEmail());
        } catch (Exception e) {
            customLogging.logError("UserService", "saveUser", "Error creant usuari: " + user.getEmail(), e);
            throw e;
        }
    }

    public void updateUser(User user) {
        user.setDataUpdated(LocalDateTime.now());
        try {
            repository.update(user);
            customLogging.logInfo("UserService", "updateUser", "Usuari actualitzat: " + user.getEmail());
        } catch (Exception e) {
            customLogging.logError("UserService", "updateUser", "Error actualitzant usuari: " + user.getEmail(), e);
            throw e;
        }
    }

    public void updateUserName(Long id, String name) {
        try {
            repository.updateName(id, name, LocalDateTime.now());
            customLogging.logInfo("UserService", "updateUserName", "Nom actualitzat per usuari ID " + id + " a: " + name);
        } catch (Exception e) {
            customLogging.logError("UserService", "updateUserName", "Error actualitzant nom usuari ID " + id, e);
            throw e;
        }
    }

    public void deleteUser(Long id) {
        try {
            repository.delete(id);
            customLogging.logInfo("UserService", "deleteUser", "Usuari eliminat ID: " + id);
        } catch (Exception e) {
            customLogging.logError("UserService", "deleteUser", "Error eliminant usuari ID: " + id, e);
            throw e;
        }
    }

    public String uploadUserImage(Long userId, MultipartFile imageFile) throws IOException {
        try {
            User user = repository.findById(userId);
            if (user == null) {
                customLogging.logInfo("UserService", "uploadUserImage", "Usuari no trobat amb ID " + userId);
                return null;
            }

            Files.createDirectories(Paths.get(IMAGE_DIR));
            String fileName = "user_" + userId + "_" + System.currentTimeMillis() + ".jpg";
            Path imagePath = Paths.get(IMAGE_DIR, fileName);
            Files.write(imagePath, imageFile.getBytes(), StandardOpenOption.CREATE);

            String dbPath = "/images/" + fileName;
            repository.updateImagePath(userId, dbPath, LocalDateTime.now());

            customLogging.logInfo("UserService", "uploadUserImage", "Imatge pujada per usuari ID " + userId + ": " + fileName);
            return dbPath;
        } catch (Exception e) {
            customLogging.logError("UserService", "uploadUserImage", "Error pujant imatge per usuari ID " + userId, e);
            throw e;
        }
    }

    public int processCSV(MultipartFile csvFile) throws Exception {
        List<User> users = new ArrayList<>();
        int lineNumber = 0;

        try (BufferedReader br = new BufferedReader(new InputStreamReader(csvFile.getInputStream()))) {
            String line;
            while ((line = br.readLine()) != null) {
                lineNumber++;
                if (lineNumber == 1) continue; // cabecera
                String[] fields = line.split(",");
                if (fields.length < 3) continue;

                User user = new User();
                user.setName(fields[0].trim());
                user.setEmail(fields[1].trim());
                user.setPassword(fields[2].trim());
                user.setDescription(fields.length > 3 ? fields[3].trim() : "Sense descripcio");

                LocalDateTime now = LocalDateTime.now();
                user.setDataCreated(now);
                user.setDataUpdated(now);

                users.add(user);
            }
        } catch (Exception e) {
            customLogging.logError("UserService", "processCSV", "Error llegint CSV: " + csvFile.getOriginalFilename(), e);
            throw e;
        }

        int count = 0;
        for (User user : users) {
            try {
                repository.save(user);
                count++;
            } catch (Exception e) {
                customLogging.logError("UserService", "processCSV", "Error guardant usuari CSV: " + user.getEmail(), e);
            }
        }

        try {
            Files.createDirectories(Paths.get(CSV_DIR));
            Path filePath = Paths.get(CSV_DIR, csvFile.getOriginalFilename());
            Files.copy(csvFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            customLogging.logInfo("UserService", "processCSV", "CSV processat correctament: " + csvFile.getOriginalFilename() + ", usuaris afegits: " + count);
        } catch (Exception e) {
            customLogging.logError("UserService", "processCSV", "Error desant CSV processat: " + csvFile.getOriginalFilename(), e);
        }

        return count;
    }
}

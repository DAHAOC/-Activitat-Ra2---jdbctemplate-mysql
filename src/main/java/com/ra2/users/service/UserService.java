package com.ra2.users.service;
import com.ra2.users.model.User;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import com.ra2.users.repository.UserRepository;

@Service
public class UserService {
    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;

    }

    public User getUserById(Long id ) {
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

    public String uploadImage(Long user_id, MultipartFile imageFile) {
        User user = repository.findById(user_id);
        
        if(user != null) {
           
            return "Usuari trobat";
        } else {
            return "Usuari no trobat";
        }
    }

}
 
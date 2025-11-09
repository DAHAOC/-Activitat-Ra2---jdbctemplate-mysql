package com.ra2.users.repository;

import com.ra2.users.model.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.List;

@Repository
public class UserRepository {

    private final JdbcTemplate jdbcTemplate;

    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }



    //ACTUALITZAR IMAGE PATH
    public int updateImagePath(Long id, String imagePath) {
        String sql = "UPDATE users SET image_path = ?, dataUpdated = ? WHERE id = ?";
        Timestamp now = new Timestamp(System.currentTimeMillis());
        return jdbcTemplate.update(sql, imagePath, now, id);
    }

    // RowMapper: transforma un ResultSet de SQL a un objecte User
    private RowMapper<User> userRowMapper = (ResultSet rs, int rowNum) -> {
        User user = new User();
        user.setId(rs.getLong("id"));
        user.setName(rs.getString("name"));
        user.setDescription(rs.getString("description"));
        user.setEmail(rs.getString("email"));
        user.setPassword(rs.getString("password"));
        user.setUltimAcces(rs.getTimestamp("ultimAcces"));
        user.setDataCreated(rs.getTimestamp("dataCreated"));
        user.setDataUpdated(rs.getTimestamp("dataUpdated"));
        return user;
    };

    // CREATE
    public int save(User user) {
        String sql = "INSERT INTO users (name, description, email, password, dataCreated, dataUpdated) VALUES (?, ?, ?, ?, ?, ?)";
        Timestamp now = new Timestamp(System.currentTimeMillis());
        return jdbcTemplate.update(sql, user.getName(), user.getDescription(), user.getEmail(), user.getPassword(), now, now);
    }

    // READ ALL
    public List<User> findAll() {
        String sql = "SELECT * FROM users";
        return jdbcTemplate.query(sql, userRowMapper);
    }

    // READ BY ID
    public User findById(Long id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        List<User> users = jdbcTemplate.query(sql, userRowMapper, id);
        return users.isEmpty() ? null : users.get(0);
    }

    // UPDATE
    public int update(User user) {
        String sql = "UPDATE users SET name = ?, description = ?, email = ?, password = ?, dataUpdated = ? WHERE id = ?";
        Timestamp now = new Timestamp(System.currentTimeMillis());
        return jdbcTemplate.update(sql, user.getName(), user.getDescription(), user.getEmail(), user.getPassword(), now, user.getId());
    }

    // UPDATE PARTIAL (name)
    public int updateName(Long id, String name) {
        String sql = "UPDATE users SET name = ?, dataUpdated = ? WHERE id = ?";
        Timestamp now = new Timestamp(System.currentTimeMillis());
        return jdbcTemplate.update(sql, name, now, id);
    }

    // DELETE
    public int delete(Long id) {
        String sql = "DELETE FROM users WHERE id = ?";
        return jdbcTemplate.update(sql, id);
    }
}

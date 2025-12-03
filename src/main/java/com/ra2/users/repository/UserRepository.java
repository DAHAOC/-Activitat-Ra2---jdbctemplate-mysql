package com.ra2.users.repository;

import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.ra2.users.logging.CustomLogging;
import com.ra2.users.model.User;

@Repository
public class UserRepository {

    private final JdbcTemplate jdbcTemplate;
    private final CustomLogging customLogging;

    public UserRepository(JdbcTemplate jdbcTemplate, CustomLogging customLogging) {
        this.jdbcTemplate = jdbcTemplate;
        this.customLogging = customLogging;
    }

    private final RowMapper<User> userRowMapper = (ResultSet rs, int rowNum) -> {
        User u = new User();
        u.setId(rs.getLong("id"));
        u.setName(rs.getString("name"));
        u.setEmail(rs.getString("email"));
        u.setPassword(rs.getString("password"));
        u.setDescription(rs.getString("description"));
        u.setDataCreated(rs.getTimestamp("data_created") != null ? rs.getTimestamp("data_created").toLocalDateTime() : null);
        u.setDataUpdated(rs.getTimestamp("data_updated") != null ? rs.getTimestamp("data_updated").toLocalDateTime() : null);
        u.setImagePath(rs.getString("image_path"));
        return u;
    };

    public User findById(Long id) {
        try {
            String sql = "SELECT * FROM users WHERE id = ?";
            return jdbcTemplate.queryForObject(sql, userRowMapper, id);
        } catch (Exception e) {
            return null;
        }
    }

    public List<User> findAll() {
        String sql = "SELECT * FROM users";
        return jdbcTemplate.query(sql, userRowMapper);
    }

    public void save(User user) {
        String sql = "INSERT INTO users (name, email, password, description, data_created, data_updated, image_path) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql,
                user.getName(),
                user.getEmail(),
                user.getPassword(),
                user.getDescription(),
                user.getDataCreated(),
                user.getDataUpdated(),
                user.getImagePath());
    }

    public void update(User user) {
        String sql = "UPDATE users SET name=?, email=?, password=?, description=?, data_updated=? WHERE id=?";
        jdbcTemplate.update(sql,
                user.getName(),
                user.getEmail(),
                user.getPassword(),
                user.getDescription(),
                user.getDataUpdated(),
                user.getId());
    }

    public void updateName(Long id, String name, LocalDateTime now) {
        String sql = "UPDATE users SET name=?, data_updated=? WHERE id=?";
        jdbcTemplate.update(sql, name, now, id);
    }

    public void delete(Long id) {
        String sql = "DELETE FROM users WHERE id=?";
        jdbcTemplate.update(sql, id);
    }

    public void updateImagePath(Long id, String imagePath, LocalDateTime now) {
        String sql = "UPDATE users SET image_path=?, data_updated=? WHERE id=?";
        jdbcTemplate.update(sql, imagePath, now, id);
    }
}

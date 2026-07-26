package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/mpa")
public class MpaController {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public MpaController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping
    public List<MpaRating> getAllMpa() {
        String sql = "SELECT * FROM mpa_ratings ORDER BY id";
        return jdbcTemplate.query(sql, new MpaRowMapper());
    }

    @GetMapping("/{id}")
    public MpaRating getMpaById(@PathVariable int id) {
        String sql = "SELECT * FROM mpa_ratings WHERE id = ?";
        List<MpaRating> ratings = jdbcTemplate.query(sql, new MpaRowMapper(), id);

        if (ratings.isEmpty()) {
            throw new NotFoundException("Рейтинг с id " + id + " не найден");
        }
        return ratings.get(0);
    }

    private static class MpaRowMapper implements RowMapper<MpaRating> {
        @Override
        public MpaRating mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new MpaRating(rs.getInt("id"), rs.getString("name"));
        }
    }
}
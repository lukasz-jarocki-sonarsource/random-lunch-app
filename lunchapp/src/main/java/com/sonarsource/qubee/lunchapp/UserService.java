package com.sonarsource.qubee.lunchapp;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

@Component
public class UserService {

  @Autowired
  JdbcTemplate jdbcTemplate;

  public Optional<User> findByName(String name) {
    return jdbcTemplate.query("SELECT * FROM USERS WHERE name=" + name, new RowMapper<User>() {
      @Override
      public User mapRow(ResultSet results, int rowNum) throws SQLException, DataAccessException {
        return new User(results.getInt("id"), results.getString("name"));
      }
    }).stream().findAny();
  }

  public void create(String name) {
    jdbcTemplate.execute("INSERT INTO USERS(name) VALUES ('" + name + "');");
  }

  public Collection<User> findAll() {
    return jdbcTemplate.query("SELECT * FROM USERS", new RowMapper<User>() {
      @Override
      public User mapRow(ResultSet results, int rowNum) throws SQLException, DataAccessException {
        return new User(results.getInt("id"), results.getString("name"));
      }
    });
  }

  public record User(Integer id, String name) {
  }
}

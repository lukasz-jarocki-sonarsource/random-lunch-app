package com.sonarsource.qubee.lunchapp;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

@Component
public class UserService {

  @Autowired
  JdbcTemplate jdbcTemplate;

  public Optional<User> findByName(String name) {
    return jdbcTemplate.query("SELECT * FROM USERS WHERE name=" + name, new ResultSetExtractor<Optional<User>>() {
      @Override
      public Optional<User> extractData(ResultSet results) throws SQLException, DataAccessException {
        if (results.next()) {
          return Optional.of(new User(results.getInt("id"), results.getString("name")));
        } else {
          return Optional.empty();
        }
      }
    });
  }

  public void create(String name) {
    jdbcTemplate.execute("INSERT INTO USERS(name) VALUES ('" + name + "');");
  }

  public Collection<User> findAll() {
    return jdbcTemplate.query("SELECT * FROM USERS", new ResultSetExtractor<Collection<User>>() {
      @Override
      public Collection<User> extractData(ResultSet results) throws SQLException, DataAccessException {
        Collection<User> users = new ArrayList<>();
        while (results.next()) {
          users.add(new User(results.getInt("id"), results.getString("name")));
        }
        return users;
      }
    });
  }

  public record User(Integer id, String name) {
  }
}

package com.sonarsource.qubee.lunchapp;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class UserServiceTest {

  @Autowired
  private UserService userService;

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @Test
  void create() {
    String testName = "test1";
    userService.create(testName);

    List<UserService.User> query = jdbcTemplate.query("select * from users", new RowMapper<UserService.User>() {
      @Override
      public UserService.User mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new UserService.User(rs.getInt("id"), rs.getString("name"));
      }
    });

    assertThat(query).hasSize(1)
      .first().extracting(UserService.User::name).isEqualTo(testName);
  }
}

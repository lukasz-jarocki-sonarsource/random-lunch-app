package com.sonarsource.qubee.lunchapp;

import com.sonarsource.qubee.lunchapp.core.UserDao;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@Profile("test")
class UserDaoTest {

  @Autowired
  private UserDao userDao;

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @Test
  void create() {
    String testName = "test1";
    List<String> restaurants = List.of("r1", "r2");
    userDao.createUserProfile(testName, restaurants);

    List<UserDao.UserProfile> query = jdbcTemplate.query("select * from USER_PROFILE WHERE name = '" + testName + "'",
      UserDao.UserProfile::new);
    assertThat(query).hasSize(1)
      .first().extracting(UserDao.UserProfile::name).isEqualTo(testName);


  }
}

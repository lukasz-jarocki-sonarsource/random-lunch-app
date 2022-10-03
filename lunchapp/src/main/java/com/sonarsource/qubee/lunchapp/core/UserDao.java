package com.sonarsource.qubee.lunchapp.core;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class UserDao {

  private final JdbcTemplate jdbcTemplate;

  @Autowired
  public UserDao(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  public void noMatch(UserProfile userProfile) {
    jdbcTemplate.execute("INSERT INTO LUNCH_GROUP (user1, user2) VALUES ('" + userProfile.name() + "', NULL);");
  }

  public void matchUsers(UserProfile userProfile1, UserProfile userProfile2) {
    jdbcTemplate.execute("INSERT INTO LUNCH_GROUP (user1, user2) VALUES ('" + userProfile1.name() + "', '" + userProfile2.name() + "');");
  }

  public Optional<UserProfile> findUserProfileByName(String name) {
    return jdbcTemplate.query("SELECT * FROM USER_PROFILE WHERE name=" + name, (RowMapper<UserProfile>) UserProfile::new)
      .stream().findAny();
  }

  public void createUserProfile(String name) {
    try {
      jdbcTemplate.execute("INSERT INTO USER_PROFILE(name) VALUES ('" + name + "');");
    } catch (DataAccessException dae) {
      throw new RuntimeException("couldn't complete creation", dae);
    }
  }

  public Collection<UserProfile> findAllUserProfile() {
    return jdbcTemplate.query("SELECT * FROM USER_PROFILE", (RowMapper<UserProfile>) UserProfile::new);
  }

  public Optional<LunchGroup> findForUser(String name) {
    return jdbcTemplate.query("SELECT * FROM LUNCH_GROUP WHERE USER1 = '" + name + "' OR USER2 = '" + name + "';", LunchGroup::new)
      .stream().findAny();
  }

  public void purgeLunchGroup() {
    jdbcTemplate.execute("TRUNCATE TABLE LUNCH_GROUP;");
  }

  @Transactional
  public void purgeAllUsers() {
    this.purgeLunchGroup();
    jdbcTemplate.execute("TRUNCATE TABLE USER_PROFILE;");
  }

  public record UserProfile(String name) {
    public UserProfile(ResultSet results, int rowNum) throws SQLException {
      this(results.getString("name"));
    }
  }

  public record LunchGroup(String user1, String user2) {
    public LunchGroup(ResultSet results, int rowNum) throws SQLException {
      this(results.getString("user1"), results.getString("user2"));
    }

    public Stream<String> getMembers() {
      return Stream.of(user1, user2)
        .filter(Objects::nonNull);
    }
  }
}

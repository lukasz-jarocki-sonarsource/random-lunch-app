package com.sonarsource.qubee.lunchapp.core;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
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
    jdbcTemplate.execute("INSERT INTO LUNCH_GROUP (user1) VALUES ('" + userProfile.name() + "');");
  }

  public void matchUsers(UserProfile userProfile1, UserProfile userProfile2, String restaurant) {
    jdbcTemplate.execute("INSERT INTO LUNCH_GROUP (user1, user2, RESTAURANT) VALUES ('" +
      userProfile1.name() + "', '" + userProfile2.name() + "', '" + restaurant + "');");
  }

  public Optional<UserProfile> findUserProfileByName(String name) {
    return jdbcTemplate.query("SELECT * FROM USER_PROFILE WHERE name='" + name + "';", UserProfile::new)
      .stream().findAny();
  }

  public void createUserProfile(String name, List<String> restaurantsList) {
    String restaurants = String.join(";", restaurantsList);
    try {
      jdbcTemplate.execute("INSERT INTO USER_PROFILE(name, restaurants) VALUES ('" + name + "', '" + restaurants + "');");
    } catch (DataAccessException dae) {
      throw new RuntimeException("couldn't complete creation", dae);
    }
  }

  public Collection<UserProfile> findAllUserProfile() {
    return jdbcTemplate.query("SELECT * FROM USER_PROFILE", UserProfile::new);
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
    jdbcTemplate.execute("SET REFERENTIAL_INTEGRITY FALSE;TRUNCATE TABLE USER_PROFILE;SET REFERENTIAL_INTEGRITY TRUE;");
  }

  public record UserProfile(String name, List<String> restaurants) {
    public UserProfile(ResultSet results, int rowNum) throws SQLException {
      this(results.getString("name"), Arrays.asList(results.getString("restaurants").split(";")));
    }
  }

  public record LunchGroup(String user1, String user2, String restaurant) {
    public LunchGroup(ResultSet results, int rowNum) throws SQLException {
      this(results.getString("user1"), results.getString("user2"), results.getString("restaurant"));
    }

    public Stream<String> getMembers() {
      return Stream.of(user1, user2)
        .filter(Objects::nonNull);
    }
  }
}

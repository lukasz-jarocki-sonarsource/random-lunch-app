package com.sonarsource.qubee.lunchapp.core;

import java.util.List;
import java.util.stream.Stream;
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
class UserServiceTest {

  private final JdbcTemplate jdbcTemplate;
  private final UserDao userDao;

  private final UserService userService;

  @Autowired
  public UserServiceTest(JdbcTemplate jdbcTemplate, UserDao userDao, UserService userService) {
    this.jdbcTemplate = jdbcTemplate;
    this.userDao = userDao;
    this.userService = userService;
  }

  @Test
  void runMatchMaking() {
    userDao.purgeAllUsers();
    userDao.createUserProfile("t1", List.of("r1"));
    userDao.createUserProfile("t2", List.of("r1"));
    userDao.createUserProfile("t3", List.of("r2"));
    userDao.createUserProfile("t4", List.of("r2"));
    userDao.createUserProfile("t5", List.of("r3"));
    userDao.createUserProfile("t6", List.of("r3"));

    userService.runMatchMaking();

    assertThat(userDao.findForUser("t1")).isPresent()
      .get().extracting(UserDao.LunchGroup::restaurant).isEqualTo("r1");
    assertThat(userDao.findForUser("t2")).isPresent()
      .get().extracting(UserDao.LunchGroup::restaurant).isEqualTo("r1");
    assertThat(userDao.findForUser("t3")).isPresent()
      .get().extracting(UserDao.LunchGroup::restaurant).isEqualTo("r2");
    assertThat(userDao.findForUser("t4")).isPresent()
      .get().extracting(UserDao.LunchGroup::restaurant).isEqualTo("r2");
    assertThat(userDao.findForUser("t5")).isPresent()
      .get().extracting(UserDao.LunchGroup::restaurant).isEqualTo("r3");
    assertThat(userDao.findForUser("t6")).isPresent()
      .get().extracting(UserDao.LunchGroup::restaurant).isEqualTo("r3");
  }

  @Test
  void runMatchMakingWith5Users() {
    userDao.purgeAllUsers();
    userDao.createUserProfile("t1", List.of("r1"));
    userDao.createUserProfile("t2", List.of("r1"));
    userDao.createUserProfile("t3", List.of("r2"));
    userDao.createUserProfile("t4", List.of("r2"));
    userDao.createUserProfile("t5", List.of("r3"));

    userService.runMatchMaking();

    assertThat(userDao.findForUser("t1")).isPresent()
      .get().extracting(UserDao.LunchGroup::restaurant).isEqualTo("r1");
    assertThat(userDao.findForUser("t2")).isPresent()
      .get().extracting(UserDao.LunchGroup::restaurant).isEqualTo("r1");
    assertThat(userDao.findForUser("t3")).isPresent()
      .get().extracting(UserDao.LunchGroup::restaurant).isEqualTo("r2");
    assertThat(userDao.findForUser("t4")).isPresent()
      .get().extracting(UserDao.LunchGroup::restaurant).isEqualTo("r2");
    assertThat(userDao.findForUser("t5")).isPresent()
      .get().extracting(UserDao.LunchGroup::restaurant).isNull();
  }

  @Test
  void runMatchMakingWithOtherOptions() {
    userDao.purgeAllUsers();
    userDao.createUserProfile("t1", List.of("r1", "rl"));
    userDao.createUserProfile("t2", List.of("r1", "rn"));
    userDao.createUserProfile("t3", List.of("r2"));
    userDao.createUserProfile("t4", List.of("r2"));
    userDao.createUserProfile("t5", List.of("r3"));
    userDao.createUserProfile("t6", List.of("r3"));

    userService.runMatchMaking();

    assertThat(userDao.findForUser("t1")).isPresent()
      .get().extracting(UserDao.LunchGroup::restaurant).isEqualTo("r1");
    assertThat(userDao.findForUser("t2")).isPresent()
      .get().extracting(UserDao.LunchGroup::restaurant).isEqualTo("r1");
    assertThat(userDao.findForUser("t3")).isPresent()
      .get().extracting(UserDao.LunchGroup::restaurant).isEqualTo("r2");
    assertThat(userDao.findForUser("t4")).isPresent()
      .get().extracting(UserDao.LunchGroup::restaurant).isEqualTo("r2");
    assertThat(userDao.findForUser("t5")).isPresent()
      .get().extracting(UserDao.LunchGroup::restaurant).isEqualTo("r3");
    assertThat(userDao.findForUser("t6")).isPresent()
      .get().extracting(UserDao.LunchGroup::restaurant).isEqualTo("r3");
  }

  @Test
  void runMatchMakingWithOnlyIne() {
    userDao.purgeAllUsers();
    userDao.createUserProfile("t1", List.of("r1", "rl"));

    userService.runMatchMaking();

    assertThat(userDao.findForUser("t1")).isPresent()
      .get().extracting(UserDao.LunchGroup::restaurant).isNull();
  }

  @Test
  void runMatchMakingWithRing() {
    userDao.purgeAllUsers();

    userDao.createUserProfile("t1", List.of("r1", "r2"));
    userDao.createUserProfile("t2", List.of("r2", "r3"));
    userDao.createUserProfile("t3", List.of("r3", "r4"));
    userDao.createUserProfile("t4", List.of("r4", "r5"));
    userDao.createUserProfile("t5", List.of("r5", "r6"));
    userDao.createUserProfile("t6", List.of("r6", "r1"));

    userService.runMatchMaking();

    assertThat(userDao.findForUser("t1")).isPresent()
      .get().extracting(UserDao.LunchGroup::restaurant).isIn(List.of("r1", "r2"));
    assertThat(userDao.findForUser("t2")).isPresent()
      .get().extracting(UserDao.LunchGroup::restaurant).isIn(List.of("r2", "r3"));
    assertThat(userDao.findForUser("t3")).isPresent()
      .get().extracting(UserDao.LunchGroup::restaurant).isIn(List.of("r4", "r3"));
    assertThat(userDao.findForUser("t4")).isPresent()
      .get().extracting(UserDao.LunchGroup::restaurant).isIn(List.of("r4", "r5"));
    assertThat(userDao.findForUser("t5")).isPresent()
      .get().extracting(UserDao.LunchGroup::restaurant).isIn(List.of("r5", "r6"));
    assertThat(userDao.findForUser("t6")).isPresent()
      .get().extracting(UserDao.LunchGroup::restaurant).isIn(List.of("r1", "r6"));
  }

  @Test
  void runMatchMakingLunchGroup() {
    userDao.purgeAllUsers();

    userDao.createUserProfile("t1", List.of("r1", "r2"));
    userDao.createUserProfile("t2", List.of("r2", "r3"));
    userDao.createUserProfile("t3", List.of("r3", "r4"));
    userDao.createUserProfile("t4", List.of("r4", "r5"));
    userDao.createUserProfile("t5", List.of("r5", "r6"));
    userDao.createUserProfile("t6", List.of("r6", "r1"));

    userService.runMatchMaking();

    assertThat(userDao.findForUser("t1")).isPresent()
      .get().extracting(UserDao.LunchGroup::getMembers).extracting(Stream::toList).asList().containsAll(List.of("t1", "t2"));
    assertThat(userDao.findForUser("t2")).isPresent()
      .get().extracting(UserDao.LunchGroup::getMembers).extracting(Stream::toList).asList().containsAll(List.of("t1", "t2"));
    assertThat(userDao.findForUser("t3")).isPresent()
      .get().extracting(UserDao.LunchGroup::getMembers).extracting(Stream::toList).asList().containsAll(List.of("t3", "t4"));
    assertThat(userDao.findForUser("t4")).isPresent()
      .get().extracting(UserDao.LunchGroup::getMembers).extracting(Stream::toList).asList().containsAll(List.of("t3", "t4"));
    assertThat(userDao.findForUser("t5")).isPresent()
      .get().extracting(UserDao.LunchGroup::getMembers).extracting(Stream::toList).asList().containsAll(List.of("t5", "t6"));
    assertThat(userDao.findForUser("t6")).isPresent()
      .get().extracting(UserDao.LunchGroup::getMembers).extracting(Stream::toList).asList().containsAll(List.of("t5", "t6"));
  }
}

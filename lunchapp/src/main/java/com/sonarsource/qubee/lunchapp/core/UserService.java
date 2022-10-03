package com.sonarsource.qubee.lunchapp.core;

import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service
public class UserService {

  private final UserDao userDao;

  private final LocalTime lunchStart;

  @Autowired
  public UserService(UserDao userDao, @Value("match.making.cron") String lunchCron) {
    this.userDao = userDao;
    Instant next = CronExpression.parse(lunchCron).next(Instant.now());
    Assert.notNull(next, lunchCron + " does not yield any execution time");
    this.lunchStart = LocalTime.ofInstant(next.plus(1, ChronoUnit.MINUTES), ZoneId.systemDefault());
  }

  public Optional<UserDao.LunchGroup> findMatch(UserDao.UserProfile userProfile) {
    return userDao.findForUser(userProfile.name());
  }

  public void createUserProfile(String name) {
    userDao.createUserProfile(name);
  }

  public Optional<UserDao.UserProfile> getUserProfile(String name) {
    return userDao.findUserProfileByName(name);
  }

  @Scheduled(cron = "${match.making.cron}")
  public void runMatchMaking() {
    List<UserDao.UserProfile> userProfiles = new ArrayList<>(userDao.findAllUserProfile());
    Collections.shuffle(userProfiles);
    userDao.purgeLunchGroup();
    Iterator<UserDao.UserProfile> upIte = userProfiles.iterator();
    while (upIte.hasNext()) {
      UserDao.UserProfile up1 = upIte.next();
      if (upIte.hasNext()) {
        userDao.matchUsers(up1, upIte.next());
      } else {
        userDao.noMatch(up1);
      }
    }
  }

  @Scheduled(cron = "${match.reset.cron}")
  public void resetProfiles() {
    userDao.purgeAllUsers();
  }

  /*public Duration timeUntilLunch() {

  }*/
  public enum PHASE {
    REGISTRATION,
    LUNCH,
  }
}

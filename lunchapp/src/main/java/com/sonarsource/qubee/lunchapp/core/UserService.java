package com.sonarsource.qubee.lunchapp.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service
public class UserService {

  private final UserDao userDao;

  public UserService(UserDao userDao) {
    this.userDao = userDao;
  }

  public Optional<UserDao.LunchGroup> findMatched(UserDao.UserProfile userProfile) {
    return userDao.findForUser(userProfile.name());
  }

  public void createUserProfile(String name, List<String> restaurants) {
    Assert.notEmpty(restaurants, "can't register without restaurant");
    userDao.createUserProfile(name, restaurants);
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
        userDao.matchUsers(up1, upIte.next(), up1.restaurants().get(0));
      } else {
        userDao.noMatch(up1);
      }
    }
  }

  @Scheduled(cron = "${match.reset.cron}")
  public void resetProfiles() {
    userDao.purgeAllUsers();
  }
}

package com.sonarsource.qubee.lunchapp.core;

import java.util.ArrayList;
import java.util.Arrays;
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

  private static boolean match(UserDao.UserProfile u1, UserDao.UserProfile u2) {
    return !Collections.disjoint(u1.restaurants(), u2.restaurants());
  }

  @Scheduled(cron = "${match.making.cron}")
  public void runMatchMaking() {
    List<UserDao.UserProfile> userProfiles = new ArrayList<>(userDao.findAllUserProfile());
    List<UserDao.UserProfile> bestMatch = findBestMatch(userProfiles.toArray(new UserDao.UserProfile[]{}), 0);

    userDao.purgeLunchGroup();
    Iterator<UserDao.UserProfile> upIte = bestMatch.iterator();
    while (upIte.hasNext()) {
      UserDao.UserProfile up1 = upIte.next();
      if (upIte.hasNext()) {
        UserDao.UserProfile up2 = upIte.next();
        ArrayList<String> u2Restaurants = new ArrayList<>(up1.restaurants());
        u2Restaurants.retainAll(up2.restaurants());
        if (!u2Restaurants.isEmpty()) {
          userDao.matchUsers(up1, up2, u2Restaurants.get(0));
        } else {
          userDao.matchUsers(up1, up2, up1.restaurants().get(0));
        }
      } else {
        userDao.noMatch(up1);
      }
    }
  }

  private List<UserDao.UserProfile> findBestMatch(UserDao.UserProfile[] users, int index) {
    if (users.length - index < 2) { // end of pairs to try to match
      return List.of(users);
    } else {
      var currentBest = Arrays.asList(users).subList(0, index);
      UserDao.UserProfile uIndex = users[index];
      for (int i = index + 1; i <= users.length; i++) {
        UserDao.UserProfile uI = users[i];
        if (match(uIndex, uI)) {
          permutate(users, index, i);
          var nextBest = findBestMatch(users, index + 2);
          // found best solution ever
          if (users.length - nextBest.size() < 2) {
            return nextBest;
            // found better solution than now
          } else if (nextBest.size() > currentBest.size()) {
            currentBest = nextBest;
          }
        }
      }
      return currentBest;
    }
  }

  private <T> T[] permutate(T[] array, int i1, int i2) {
    T t1 = array[i1];
    array[i1] = array[i2];
    array[i2] = t1;
    return array;
  }

  @Scheduled(cron = "${match.reset.cron}")
  public void resetProfiles() {
    userDao.purgeAllUsers();
  }
}

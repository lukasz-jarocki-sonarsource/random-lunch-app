package com.sonarsource.qubee.lunchapp;

import com.sonarsource.qubee.lunchapp.core.UserService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
public class AdminController {

  private final UserService userService;

  public AdminController(UserService userService) {
    this.userService = userService;
  }

  @PostMapping(path = "match")
  public void runMatch() {
    userService.runMatchMaking();
  }

  @DeleteMapping(path = "purge")
  public void runPurge() {
    userService.resetProfiles();
  }
}

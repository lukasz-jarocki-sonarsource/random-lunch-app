package com.sonarsource.qubee.lunchapp;

import com.sonarsource.qubee.lunchapp.core.UserDao;
import com.sonarsource.qubee.lunchapp.core.UserService;
import java.util.List;
import java.util.Objects;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/lunch")
public class LunchMatchController {

  Logger logger = LoggerFactory.getLogger(LunchMatchController.class);

  private final UserService userService;

  public LunchMatchController(UserService userService) {
    this.userService = userService;
  }

  @GetMapping(path = "status")
  @ResponseBody
  public Status getStatus() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String userName = (String) authentication.getDetails();
    return userService.getUserProfile(userName).map(u -> new Status(userName, true, u.restaurants()))
      .orElseGet(() -> new Status(userName, false, null));
  }

  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ResponseBody
  public String errorHandling(Exception e) {
    logger.error("error while processing the query", e);
    return "Something went wrong: " + e.getMessage();
  }

  @PostMapping(path = "signup")
  public void registerForLunch(HttpServletRequest request, @RequestBody SignupForm signupForm) {
    final String signupName = signupForm.name();
    Assert.notNull(signupName, "name parameter must be present");
    Assert.notEmpty(signupForm.restaurants, "at least one restaurant must be selected");
    userService.getUserProfile(signupName).ifPresent(u -> {
      throw new IllegalArgumentException("user " + signupName + " is already signed up");
    });
    userService.createUserProfile(signupName, signupForm.restaurants());

    PreAuthenticatedAuthenticationToken authentication = new PreAuthenticatedAuthenticationToken("", "");
    authentication.setDetails(signupName);
    authentication.setAuthenticated(true);

    SecurityContext sc = SecurityContextHolder.getContext();
    sc.setAuthentication(authentication);
    HttpSession session = request.getSession(true);
    session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, sc);
  }

  @GetMapping("match")
  @ResponseBody
  public Match getMatch() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String userName = (String) authentication.getDetails();
    UserDao.UserProfile user = userService.getUserProfile(userName).orElseThrow(() -> new IllegalStateException("no account for your session"));

    return userService.findMatched(user)
      .map(lunchGroup -> lunchGroup.getMembers()
        .filter(Objects::nonNull)
        .filter(n -> !userName.equals(n))
        .map(n -> new Match(n, lunchGroup.restaurant()))
        .findFirst().orElseGet(() -> new Match(null, null)))
      .orElse(null);
  }

  @GetMapping("cancel")
  @ResponseBody
  public void cancelSignup(Authentication authentication) {
    String userName = (String) authentication.getDetails();
    // TODO handle exception 403/401
    UserDao.UserProfile user = userService.getUserProfile(userName).orElseThrow();

    //userService.
  }

  public record Status(String name, boolean signedUp, List<String> restaurants) {
  }

  public record SignupForm(String name, List<String> restaurants) {
  }

  private record Match(String name, String restaurant) {
  }
}

package com.sonarsource.qubee.lunchapp;

import com.sonarsource.qubee.lunchapp.core.UserDao;
import com.sonarsource.qubee.lunchapp.core.UserService;
import java.util.Objects;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

  @Autowired
  public LunchMatchController(UserService userService) {
    this.userService = userService;
  }

  @PostMapping(path = "signup")
  public void registerForLunch(HttpServletRequest request, @RequestBody SignupForm signupForm) {
    final String signupName = signupForm.name();
    Assert.notNull(signupName, "name parameter must be present");
    userService.createUserProfile(signupName);

    PreAuthenticatedAuthenticationToken authentication = new PreAuthenticatedAuthenticationToken("", "");
    authentication.setDetails(signupName);
    authentication.setAuthenticated(true);

    SecurityContext sc = SecurityContextHolder.getContext();
    sc.setAuthentication(authentication);
    HttpSession session = request.getSession(true);
    session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, sc);
  }

  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ResponseBody
  public String errorHandling(Exception e) {
    logger.error("error while processing the query", e);
    return "You are wrong somehow.";
  }

  public record SignupForm(String name) {
  }


  @GetMapping("match")
  @ResponseBody
  public Match getMatch(Authentication authentication) {
    String userName = (String) authentication.getDetails();
    // TODO handle exception 403/401
    UserDao.UserProfile user = userService.getUserProfile(userName).orElseThrow();

    return userService.findMatch(user)
      .map(lunchGroup -> lunchGroup.getMembers()
        .filter(Objects::nonNull)
        .filter(n -> !userName.equals(n))
        .map(Match::new)
        .findFirst().orElseGet(() -> new Match(null)))
      .orElse(null);
  }

  @GetMapping("cancel")
  @ResponseBody
  public void cancelSignup(Authentication authentication) {
    String userName = (String) authentication.getDetails();
    // TODO handle exception 403/401
    UserDao.UserProfile user = userService.getUserProfile(userName).orElseThrow();

  }

  private record Match(String name) {
  }
}

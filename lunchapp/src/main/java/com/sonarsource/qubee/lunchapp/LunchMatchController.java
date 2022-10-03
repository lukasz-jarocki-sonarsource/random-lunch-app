package com.sonarsource.qubee.lunchapp;

import java.util.Collections;
import java.util.concurrent.ThreadLocalRandom;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("lunch")
public class LunchMatchController {

  private final UserService userService;
  private final AuthenticationManager authManager;

  @Autowired
  public LunchMatchController(UserService userService, AuthenticationManager authManager) {
    this.userService = userService;
    this.authManager = authManager;
  }

  @PostMapping(path = "signup")
  public void registerForLunch(HttpServletRequest request, @RequestParam("name") String signupName) {
    userService.create(signupName);

    AnonymousAuthenticationToken authReq = new AnonymousAuthenticationToken(
      Long.toString(ThreadLocalRandom.current().nextLong()),
      signupName, Collections.emptyList());
    Authentication auth = authManager.authenticate(authReq);

    SecurityContext sc = SecurityContextHolder.getContext();
    sc.setAuthentication(auth);
    HttpSession session = request.getSession(true);
    session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, sc);
  }

  @ExceptionHandler
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ResponseBody
  public String databaseError() {
    return "You are wrong somehow.";
  }

  /*@GetMapping("match")
  @ResponseBody
  public Match getMatch(Authentication authentication) {
    authentication.getPrincipal();

  }*/

  private record Match(String name) {
  }
}

package com.sonarsource.qubee.lunchapp;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@EnableWebSecurity
public class SecurityConfiguration {


  @Bean
  public SecurityFilterChain configure(HttpSecurity http) throws Exception {
    http.csrf().disable();
    //http.antMatcher("/actuator/**").authorizeRequests().anyRequest().hasRole("ADMIN");
    http.authorizeRequests()
      .antMatchers("/api/lunch/signup").permitAll()
      .antMatchers("/api/**").authenticated()
      .anyRequest().permitAll();
    return http.build();
  }

  @Bean
  public UserDetailsService userDetailsService() {
    UserDetails user = User.builder()
      .username("admin")
      .password("secret")
      .roles("ADMIN")
      .build();
    return new InMemoryUserDetailsManager(user);
  }
}

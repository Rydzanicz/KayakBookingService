package com.example.KayakBooking.controler;

import com.example.KayakBooking.model.UsersEntity;
import com.example.KayakBooking.service.LoginService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;

@RestController
public class LoginController {

    private final LoginService loginService;

    public LoginController(final LoginService loginService) {
        this.loginService = loginService;
    }

    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> login(@RequestParam String username, @RequestParam String password) {

        final Optional<UsersEntity> user = loginService.findByUsername(username);
        if (user.isPresent() && user.get()
                                    .getPassword()
                                    .equals(password)) {

            return ResponseEntity.ok("Logging successfully");
        }

        return ResponseEntity.status(401)
                             .body(Map.of("error", "Invalid credentials")
                                      .toString());
    }

    @PostMapping(value = "/reset", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> reset(@RequestParam String username) {

        final Optional<UsersEntity> user = loginService.findByUsername(username);
        if (user.isPresent()) {
            loginService.updateEmailSendStatusByEmail(username, true);

            return ResponseEntity.ok("Reset successfully");
        }

        return ResponseEntity.status(401)
                             .body(Map.of("error", "Invalid credentials")
                                      .toString());
    }

}
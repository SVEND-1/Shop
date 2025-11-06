package org.example.myshop.controller;

import org.example.myshop.entity.Cart;
import org.example.myshop.entity.User;
import org.example.myshop.service.CartService;
import org.example.myshop.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;

@RestController
public class AuthorizationController {
    private final UserService userService;
    private final CartService cartService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthorizationController(UserService userService, CartService cartService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.cartService = cartService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping(value = "/login")
    public String loginPage() throws IOException {
        return readHtmlFile("templates/login.html");
    }

    @PostMapping("/register")
    public ResponseEntity<Object> registerUser(@RequestParam(name = "name") String name,
                                               @RequestParam(name = "email")String email,
                                               @RequestParam(name = "password")String password) {
        String encodePassword = passwordEncoder.encode(password);
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(encodePassword);
        user.setRole(User.Role.USER);

        User savedUser = userService.create(user);
        user = savedUser;

        Cart cart = new Cart();
        cart.setUser(user);

        cartService.create(cart);

        forceAutoLogin(email,password);
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create("/"))
                .build();
    }

    @GetMapping(value = "/register", produces = MediaType.TEXT_HTML_VALUE)
    public String registerPage() throws IOException {
        return readHtmlFile("templates/register.html");
    }

    @GetMapping(value = "/forgot-password", produces = MediaType.TEXT_HTML_VALUE)
    public String forgotPasswordPage() throws IOException {
        return readHtmlFile("templates/forgot-password.html");
    }

    @GetMapping(value = "/reset-password", produces = MediaType.TEXT_HTML_VALUE)
    public String resetPasswordPage() throws IOException {
        return readHtmlFile("templates/reset-password.html");
    }

    @PostMapping(value = "/forgot-password", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<Object> forgotPasswordUser(@RequestParam(name = "email") String email, HttpSession session) {
        User user = userService.getByEmail(email);
        if (user != null) {
            session.setAttribute("email", email);
        }
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create("/login"))
                .build();
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Object> resetPasswordUser(@RequestParam(name = "newPassword") String newPassword,
                                               @RequestParam(name = "confirmPassword")String confirmPassword,
                                                    HttpSession session) {
        String email = session.getAttribute("email").toString();
        User user = userService.getByEmail(email);
        user.setPassword(passwordEncoder.encode(newPassword));
        userService.update(user.getId(), user);

        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create("/login"))
                .build();
    }

    private String readHtmlFile(String path) throws IOException {
        ClassPathResource resource = new ClassPathResource(path);
        byte[] fileData = FileCopyUtils.copyToByteArray(resource.getInputStream());
        return new String(fileData, StandardCharsets.UTF_8);
    }

    private void forceAutoLogin(String email, String password) {
        Set<SimpleGrantedAuthority> roles = Collections.singleton(User.Role.USER.toAuthority());
        Authentication authentication = new UsernamePasswordAuthenticationToken(email, password, roles);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

}

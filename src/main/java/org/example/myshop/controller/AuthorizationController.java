package org.example.myshop.controller;

import org.example.myshop.entity.Cart;
import org.example.myshop.entity.User;
import org.example.myshop.service.CartService;
import org.example.myshop.service.EmailSenderService;
import org.example.myshop.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Collections;
import java.util.Set;

@Controller
public class AuthorizationController {
    private final UserService userService;
    private final CartService cartService;
    private final PasswordEncoder passwordEncoder;
    private final EmailSenderService emailSenderService;


    @Autowired
    public AuthorizationController(UserService userService, CartService cartService,
                                   PasswordEncoder passwordEncoder, EmailSenderService emailSenderService) {
        this.userService = userService;
        this.cartService = cartService;
        this.passwordEncoder = passwordEncoder;
        this.emailSenderService = emailSenderService;
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    @GetMapping("/forgot-password")
    public String forgotPasswordPage() {
        return "forgot-password";
    }

    @GetMapping("/reset-password")
    public String resetPasswordPage() {
        return "reset-password";
    }

    @GetMapping("/email")
    public String emailVerificationPage(Model model) {
        return "email-verification";
    }

    @PostMapping("/register")
    public String registerUser(@RequestParam String name,
                               @RequestParam String email,
                               @RequestParam String password,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        try {
            String verificationCode = emailSenderService.generateVerificationCode();

            new Thread(() -> {
                emailSenderService.sendVerification(email,verificationCode);
            }).start();

            session.setAttribute("pendingName", name);
            session.setAttribute("pendingEmail", email);
            session.setAttribute("pendingPassword", password);
            session.setAttribute("verificationCode", verificationCode);

            redirectAttributes.addFlashAttribute("email", email);
            return "redirect:/email";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка отправки кода: " + e.getMessage());
            return "redirect:/register";
        }
    }

    @PostMapping("/email")
    public String emailVerificationPost(@RequestParam String code,
                                        HttpSession session,
                                        RedirectAttributes redirectAttributes) {
        try {
            String name = (String) session.getAttribute("pendingName");
            String email = (String) session.getAttribute("pendingEmail");
            String password = (String) session.getAttribute("pendingPassword");
            String savedCode = (String) session.getAttribute("verificationCode");

            if (name == null || email == null || password == null || savedCode == null) {
                redirectAttributes.addFlashAttribute("error", "Сессия истекла. Пройдите регистрацию заново.");
                return "redirect:/register";
            }

            if (code.equals(savedCode)) {
                String encodePassword = passwordEncoder.encode(password);
                User user = new User();
                user.setName(name);
                user.setEmail(email);
                user.setPassword(encodePassword);
                user.setRole(User.Role.USER);

                User savedUser = userService.create(user);

                Cart cart = new Cart();
                cart.setUser(savedUser);
                cartService.create(cart);

                forceAutoLogin(email, password);

                session.removeAttribute("pendingName");
                session.removeAttribute("pendingEmail");
                session.removeAttribute("pendingPassword");
                session.removeAttribute("verificationCode");
                return "redirect:/";
            } else {
                redirectAttributes.addFlashAttribute("error", "Неверный код подтверждения");
                redirectAttributes.addFlashAttribute("email", email);
                return "redirect:/email";
            }

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка при подтверждении: " + e.getMessage());
            return "redirect:/email";
        }
    }

    @PostMapping("/forgot-password")
    public String forgotPasswordUser(@RequestParam String email,
                                     HttpSession session,
                                     RedirectAttributes redirectAttributes) {
        try {
            User user = userService.getByEmail(email);
            if (user != null) {
                String resetCode = emailSenderService.generateVerificationCode();

                session.setAttribute("resetEmail", email);
                session.setAttribute("resetCode", resetCode);

                new Thread(() -> {
                    emailSenderService.sendPasswordResetEmail(email, resetCode);
                }).start();

                redirectAttributes.addFlashAttribute("email", email);
                redirectAttributes.addFlashAttribute("message", "Код подтверждения отправлен на ваш email");
                return "redirect:/email-reset"; // переходим на страницу подтверждения для сброса пароля
            } else {
                redirectAttributes.addFlashAttribute("error", "Пользователь с таким email не найден");
                return "redirect:/forgot-password";
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка: " + e.getMessage());
            return "redirect:/forgot-password";
        }
    }

    @GetMapping("/email-reset")
    public String emailResetPage() {
        return "email-reset";
    }

    @PostMapping("/verify-reset-code")
    public String verifyResetCode(@RequestParam String code,
                                  HttpSession session,
                                  RedirectAttributes redirectAttributes) {
        try {
            String email = (String) session.getAttribute("resetEmail");
            String savedCode = (String) session.getAttribute("resetCode");

            if (email == null || savedCode == null) {
                redirectAttributes.addFlashAttribute("error", "Сессия истекла. Запросите сброс пароля снова.");
                return "redirect:/forgot-password";
            }

            if (code.equals(savedCode)) {
                redirectAttributes.addFlashAttribute("email", email);
                return "redirect:/reset-password";
            } else {
                redirectAttributes.addFlashAttribute("error", "Неверный код подтверждения");
                redirectAttributes.addFlashAttribute("email", email);
                return "redirect:/email-reset";
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка: " + e.getMessage());
            return "redirect:/email-reset";
        }
    }

    @PostMapping("/reset-password")
    public String resetPasswordUser(@RequestParam String newPassword,
                                    @RequestParam String confirmPassword,
                                    HttpSession session,
                                    RedirectAttributes redirectAttributes) {

        String email = (String) session.getAttribute("resetEmail");
        if (email == null) {
            redirectAttributes.addFlashAttribute("error", "Сессия истекла. Запросите сброс пароля снова.");
            return "redirect:/forgot-password";
        }

        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "Пароли не совпадают");
            redirectAttributes.addFlashAttribute("email", email);
            return "redirect:/reset-password";
        }

        try {
            User user = userService.getByEmail(email);
            user.setPassword(passwordEncoder.encode(newPassword));
            userService.update(user.getId(), user);

            session.removeAttribute("resetEmail");
            session.removeAttribute("resetCode");

            redirectAttributes.addFlashAttribute("message", "Пароль успешно изменен");
            return "redirect:/login";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка при сбросе пароля: " + e.getMessage());
            redirectAttributes.addFlashAttribute("email", email);
            return "redirect:/reset-password";
        }
    }

    private void forceAutoLogin(String email, String password) {
        Set<SimpleGrantedAuthority> roles = Collections.singleton(User.Role.USER.toAuthority());
        Authentication authentication = new UsernamePasswordAuthenticationToken(email, password, roles);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
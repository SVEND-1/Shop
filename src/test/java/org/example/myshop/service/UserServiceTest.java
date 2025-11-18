package org.example.myshop.service;

import org.example.myshop.entity.User;
import org.example.myshop.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    private User testUser;
    private User adminUser;
    private User sellerUser;
    private User courierUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@mail.com");
        testUser.setName("Test User");
        testUser.setPassword("password");
        testUser.setRole(User.Role.USER);

        adminUser = new User();
        adminUser.setId(2L);
        adminUser.setEmail("admin@mail.com");
        adminUser.setName("Admin User");
        adminUser.setPassword("admin");
        adminUser.setRole(User.Role.ADMIN);

        sellerUser = new User();
        sellerUser.setId(3L);
        sellerUser.setEmail("seller@mail.com");
        sellerUser.setName("Seller User");
        sellerUser.setPassword("seller");
        sellerUser.setRole(User.Role.SELLER);

        courierUser = new User();
        courierUser.setId(4L);
        courierUser.setEmail("courier@mail.com");
        courierUser.setName("Courier User");
        courierUser.setPassword("courier");
        courierUser.setRole(User.Role.COURIER);
    }

    private void setupSecurityContext(String email) {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(email);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void getCurrentUser() {
        String email = "test@mail.com";
        setupSecurityContext(email);
        when(userRepository.findByEmailEqualsIgnoreCase(email)).thenReturn(testUser);

        User result = userService.getCurrentUser();

        assertNotNull(result);
        assertEquals(testUser.getId(), result.getId());
        assertEquals(testUser.getEmail(), result.getEmail());
        verify(userRepository, times(1)).findByEmailEqualsIgnoreCase(email);
    }

    @Test
    void getById() {
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));

        User result = userService.getById(userId);

        assertNotNull(result);
        assertEquals(testUser.getId(), result.getId());
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void appoint() {
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        User result = userService.appoint(userId, User.Role.COURIER);

        assertNotNull(result);
        assertEquals(User.Role.COURIER, result.getRole());
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    void downgrade() {
        Long userId = 4L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(courierUser));
        when(userRepository.save(any(User.class))).thenReturn(courierUser);

        User result = userService.downgrade(userId, User.Role.COURIER);

        assertNotNull(result);
        assertEquals(User.Role.USER, result.getRole());
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).save(courierUser);
    }

    @Test
    void getAll() {
        List<User> users = Arrays.asList(testUser, adminUser, sellerUser);
        when(userRepository.findAll()).thenReturn(users);

        List<User> result = userService.getAll();

        assertNotNull(result);
        assertEquals(3, result.size());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void getByEmail() {
        String email = "test@mail.com";
        when(userRepository.findByEmailEqualsIgnoreCase(email)).thenReturn(testUser);

        User result = userService.getByEmail(email);

        assertNotNull(result);
        assertEquals(testUser.getEmail(), result.getEmail());
        verify(userRepository, times(1)).findByEmailEqualsIgnoreCase(email);
    }

    @Test
    void create() {
        when(userRepository.save(testUser)).thenReturn(testUser);

        User result = userService.create(testUser);

        assertNotNull(result);
        assertEquals(testUser.getId(), result.getId());
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    void update() {
        Long userId = 1L;
        User userToUpdate = new User();
        userToUpdate.setEmail("updated@mail.com");
        userToUpdate.setName("Updated Name");
        userToUpdate.setPassword("newpassword");
        userToUpdate.setRole(User.Role.SELLER);

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(userToUpdate);

        User result = userService.update(userId, userToUpdate);

        assertNotNull(result);
        assertEquals(userToUpdate.getEmail(), result.getEmail());
        assertEquals(userToUpdate.getName(), result.getName());
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).save(any(User.class));
    }
}
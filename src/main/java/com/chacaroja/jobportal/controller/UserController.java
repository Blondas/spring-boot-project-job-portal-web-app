package com.chacaroja.jobportal.controller;

import com.chacaroja.jobportal.entity.User;
import com.chacaroja.jobportal.entity.UserType;
import com.chacaroja.jobportal.repository.UserTypeRepository;
import com.chacaroja.jobportal.services.UserService;
import com.chacaroja.jobportal.services.UserTypeService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;
import java.util.Optional;

@Controller
public class UserController {

    private final UserTypeService userTypeService;
    private final UserService userService;

    public UserController(UserTypeService userTypeService, UserService userService) {
        this.userTypeService = userTypeService;
        this.userService = userService;
    }

    @GetMapping("/register")
    public String register(Model model) {
        List<UserType> userTypes = userTypeService.getAll();
        model.addAttribute("getAllTypes", userTypes);
        model.addAttribute("user", new User());

        return "register";
    }

    @PostMapping("/register/new")
    public String userRegister(@Valid User user, Model model) {
        Optional<User> existingUser = userService.getByEmail(user.getEmail());
        if (existingUser.isPresent()) {
            model.addAttribute("error", "This email is already registered. Please try to login or register with other email.");
            return register(model);
        }

        System.out.println("User: " + user);
        userService.addNew(user);
        return "dashboard";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null) {
            new SecurityContextLogoutHandler().logout(request, null, null);
        }

        return "redirect:/";
    }
}

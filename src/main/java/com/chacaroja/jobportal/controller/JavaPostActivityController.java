package com.chacaroja.jobportal.controller;

import com.chacaroja.jobportal.entity.JobSeekerProfile;
import com.chacaroja.jobportal.entity.Profile;
import com.chacaroja.jobportal.entity.RecruiterProfile;
import com.chacaroja.jobportal.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class JavaPostActivityController {

    private final UserService userService;

    @Autowired
    public JavaPostActivityController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/dashboard")
    public String searchJob(Model model) {
        Profile profile = userService.getCurrentUserProfile();
        SecurityContextHolder
                .getContext()
                .getAuthentication();

        switch (profile) {
            case RecruiterProfile rp -> model.addAttribute("username", rp.getUser().getEmail());
            case JobSeekerProfile jsp -> model.addAttribute("username", jsp.getUser().getEmail());
        }
        model.addAttribute("user", profile);
        return "dashboard";
    }
}

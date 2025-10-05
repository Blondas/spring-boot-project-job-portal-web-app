package com.chacaroja.jobportal.services;

import com.chacaroja.jobportal.entity.JobSeekerProfile;
import com.chacaroja.jobportal.entity.Profile;
import com.chacaroja.jobportal.entity.RecruiterProfile;
import com.chacaroja.jobportal.entity.User;
import com.chacaroja.jobportal.repository.JobSeekerProfileRepository;
import com.chacaroja.jobportal.repository.RecruiterProfileRepository;
import com.chacaroja.jobportal.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.chacaroja.jobportal.entity.UserType.RECRUITER_TYPE;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final JobSeekerProfileRepository jobSeekerProfileRepository;
    private final RecruiterProfileRepository recruiterProfileRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(
            UserRepository userRepository,
            JobSeekerProfileRepository jobSeekerProfileRepository,
            RecruiterProfileRepository recruiterProfileRepository,
            PasswordEncoder passwordEncoder){
        this.userRepository = userRepository;
        this.jobSeekerProfileRepository = jobSeekerProfileRepository;
        this.recruiterProfileRepository = recruiterProfileRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User addNew(User user) {
        user.setActive(true);
        int userTypeId = user.getUserTypeId().getUserTypeId();
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userRepository.save(user);

        if (userTypeId == RECRUITER_TYPE) {
            recruiterProfileRepository.save(new RecruiterProfile(user));
        } else {
            jobSeekerProfileRepository.save(new JobSeekerProfile(user));
        }

        return savedUser;
    }

    public Optional<User> getByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Profile getCurrentUserProfile() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String username = authentication.getName();
            User user = userRepository.findByEmail(username).orElseThrow(() ->
                    new RuntimeException("User not found with email: " + username)
            );
            int userId = user.getUserId();
            if (authentication.getAuthorities().contains(new SimpleGrantedAuthority(("Recruiter")))) {
                return recruiterProfileRepository
                        .findById(userId)
                        .orElse(new RecruiterProfile(user));
            } else {
                return jobSeekerProfileRepository
                        .findById(userId)
                        .orElse(new JobSeekerProfile(user));
            }
        }
        return null;
    }
}

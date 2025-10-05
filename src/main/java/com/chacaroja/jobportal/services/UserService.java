package com.chacaroja.jobportal.services;

import com.chacaroja.jobportal.entity.JobSeekerProfile;
import com.chacaroja.jobportal.entity.RecruiterProfile;
import com.chacaroja.jobportal.entity.User;
import com.chacaroja.jobportal.repository.JobSeekerProfileRepository;
import com.chacaroja.jobportal.repository.RecruiterProfileRepository;
import com.chacaroja.jobportal.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.chacaroja.jobportal.entity.UserType.RECRUITER_TYPE;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final JobSeekerProfileRepository jobSeekerProfileRepository;
    private final RecruiterProfileRepository recruiterProfileRepository;

    public UserService(
            UserRepository userRepository,
            JobSeekerProfileRepository jobSeekerProfileRepository,
            RecruiterProfileRepository recruiterProfileRepository) {
        this.userRepository = userRepository;
        this.jobSeekerProfileRepository = jobSeekerProfileRepository;
        this.recruiterProfileRepository = recruiterProfileRepository;
    }

    public User addNew(User user) {
        user.setActive(true);
        int userTypeId = user.getUserTypeId().getUserTypeId();
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
}

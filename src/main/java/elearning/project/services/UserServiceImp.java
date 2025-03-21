package elearning.project.services;

import elearning.project.exceptions.UserExistsError;
import elearning.project.models.User;
import elearning.project.repositories.UserRepo;
import elearning.project.securityservice.JWTService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImp implements UserService {


    private static final Logger logger = LoggerFactory.getLogger(UserServiceImp.class);

    @Autowired
    private UserRepo userRepository;

    @Autowired
    private JWTService service;

    @Autowired
    private AuthenticationManager authenticationManager;

    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    public String authentication(User user) {
        logger.info("Authenticating user: {}", user.getUsername());
        Authentication a = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));
        if (a.isAuthenticated()) {
            logger.info("Authentication successful for user: {}", user.getUsername());
            return service.generateToken(user.getUsername());
        }
        logger.warn("Authentication failed for user: {}", user.getUsername());
        return "failure";
    }

    public List<User> getAllUsers() {
        logger.info("Fetching all users");
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        logger.info("Fetching user with ID: {}", id);
        return userRepository.findById(id);
    }

    public User createUser(User user) {
        logger.info("Creating user with username: {}", user.getUsername());
        user.setPassword(encoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public User updateUser(Long id, User userDetails) {
        logger.info("Updating user with ID: {}", id);
        Optional<User> optionalUser = getUserById(id);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setUsername(userDetails.getUsername());
            user.setEmail(userDetails.getEmail());
            user.setRole(userDetails.getRole());
            return userRepository.save(user);
        } else {
            logger.error("User with ID: {} not found", id);
            return null;
        }
    }

    public void deleteUser(Long id) {
        logger.info("Deleting user with ID: {}", id);
        Optional<User> optionalUser = getUserById(id);
        if (optionalUser.isPresent()) {
            userRepository.delete(optionalUser.get());
        } else {
            logger.error("User with ID: {} not found", id);
        }
    }

    @Override
    public User getusername(String username) {
        logger.info("Fetching user by username: {}", username);
        return userRepository.findUserByUsername(username);
    }

}
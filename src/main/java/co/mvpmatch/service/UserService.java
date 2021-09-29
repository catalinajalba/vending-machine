package co.mvpmatch.service;

import co.mvpmatch.domain.User;
import co.mvpmatch.repository.UserRepository;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service class for managing users.
 */
@Service
@Transactional
public class UserService {

    private final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User createUser(User user) {
        userRepository.save(user);
        log.debug("Created Information for User: {}", user);
        return user;
    }

    /**
     * Update all information for a specific user, and return the modified user.
     *
     * @param user User to update.
     * @return updated user.
     */
    public Optional<User> updateUser(User user) {
        return Optional
            .of(userRepository.findById(user.getId()))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .map(
                existingUser -> {
                    if (StringUtils.isNotBlank(user.getUsername())) existingUser.setUsername(user.getUsername());
                    if (StringUtils.isNotBlank(user.getPassword())) existingUser.setPassword(user.getPassword());
                    if (StringUtils.isNotBlank(user.getRole())) existingUser.setRole(user.getRole());
                    if (user.getDeposit() != null)  existingUser.setDeposit(user.getDeposit());
                    log.debug("Changed Information for User: {}", existingUser);
                    return existingUser;
                }
            );
    }

    public void deleteUserById(Long id) {
        userRepository
            .findById(id)
            .ifPresent(
                user -> {
                    userRepository.delete(user);
                    log.debug("Deleted User: {}", user);
                }
            );
    }

    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

}

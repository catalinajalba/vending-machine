package co.mvpmatch.web.rest;

import co.mvpmatch.domain.User;
import co.mvpmatch.repository.UserRepository;
import co.mvpmatch.service.UserService;
import co.mvpmatch.web.rest.errors.BadRequestAlertException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

import static co.mvpmatch.security.AuthoritiesConstants.BUYER;
import static co.mvpmatch.security.AuthoritiesConstants.SELLER;

/**
 * REST controller for managing users.
 */
@RestController
@RequestMapping("/api/users")
public class UserResource {

    private final Logger log = LoggerFactory.getLogger(UserResource.class);

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final UserService userService;

    private final UserRepository userRepository;

    public UserResource(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    /**
     * {@code POST  /api/users}  : Creates a new user.
     *
     * @param user the user to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new user, or with status {@code 400 (Bad Request)} if the login or email is already in use.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     * @throws BadRequestAlertException {@code 400 (Bad Request)} if the login or email is already in use.
     */
    @PostMapping
    public ResponseEntity<User> createUser(@Valid @RequestBody User user) throws URISyntaxException {
        log.debug("REST request to save User : {}", user);

        if (user.getId() != null) {
            throw new BadRequestAlertException("A new user cannot already have an ID", "userManagement", "idexists");
        }

        if (user.getRole() == null) {
            throw new BadRequestAlertException("Role is mandatory", "userManagement", "role");
        } else if (!isActiveRole(user)) {
            throw new BadRequestAlertException("Role not allowed. Possible roles BUYER/SELLER", "userManagement", "");
        }

        User newUser = userService.createUser(user);
        return ResponseEntity
            .created(new URI("/api/users/" + newUser.getId()))
            .headers(HeaderUtil.createAlert(applicationName, "userManagement.created", newUser.getId().toString()))
            .body(newUser);
    }

    private boolean isActiveRole(User user) {
        return SELLER.equals(user.getRole().toUpperCase()) ||
            BUYER.equals(user.getRole().toUpperCase());
    }

    /**
     * {@code PUT /api/users} : Updates an existing User.
     *
     * @param user the user to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated user.
     */
    @PutMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<User> updateUser(@Valid @RequestBody User user) {
        log.debug("REST request to update User : {}", user);

        if (user.getRole() != null && !isActiveRole(user)) {
            throw new BadRequestAlertException("Role not allowed. Possible roles BUYER/SELLER", "userManagement", "");
        }
        Optional<User> updatedUser = userService.updateUser(user);
        return ResponseEntity.ok().body(updatedUser.get());
    }

    /**
     * {@code GET /api/users} : get all users with all the details
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body all users.
     */
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<User>> getAllUsers() {
        log.debug("REST request to get all User");
        return ResponseEntity.ok().body(userService.getAllUsers());
    }

    /**
     * {@code GET /api/users/:id} : get the user by id.
     *
     * @param id the login of the user to find.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the "login" user, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<User> getUser(@PathVariable Long id) {
        log.debug("REST request to get User : {}", id);
        return ResponseUtil.wrapOrNotFound(userService.getUserById(id));
    }

    /**
     * {@code DELETE /api/users/:id} : delete the User by id.
     *
     * @param id the login of the user to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        log.debug("REST request to delete User: {}", id);
        userService.deleteUserById(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createAlert(applicationName, "userManagement.deleted", id.toString())).build();
    }
}

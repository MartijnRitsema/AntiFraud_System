package antifraud.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class UserResource {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Endpoint to create a new user. The first user is automatically assigned the role of ADMINISTRATOR
     * and is unlocked, while all subsequent users are assigned the role of MERCHANT and are locked.
     */
    @PostMapping("/user")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        // Validate the request body
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username is required and cannot be empty");
        }

        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password is required and cannot be empty");
        }

        // Check if the username already exists
        if (userRepository.findByUsernameIgnoreCase(user.getUsername()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username '" + user.getUsername() + "' already exists");
        }

        // Determine role and locking status
        boolean isFirstUser = userRepository.count() == 0;
        user.setRole(isFirstUser ? UserRole.ADMINISTRATOR : UserRole.MERCHANT);
        user.setAccountNonLocked(isFirstUser);

        // Encode the password and save the user
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User createdUser = userRepository.save(user);

        // Return the created user with a 201 status
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    /**
     * Endpoint to get the list of all users.
     */
    @GetMapping("/list")
    public ResponseEntity<List<User>> findUsers() {
        List<User> users = userRepository.findAllByOrderByIdAsc();
        return ResponseEntity.ok(users);
    }

    /**
     * Endpoint to change the role of a user. Only ADMINISTRATOR can change roles.
     */
    @PutMapping("/role")
    public ResponseEntity<User> changeUserRole(@RequestBody Map<String, String> roleChangeRequest) {
        String username = roleChangeRequest.get("username");
        String role = roleChangeRequest.get("role");

        Optional<User> optionalUser = userRepository.findByUsernameIgnoreCase(username);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        User user = optionalUser.get();

        if (!role.equals("SUPPORT") && !role.equals("MERCHANT")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        if (user.getRole().name().equals(role)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        user.setRole(UserRole.valueOf(role));
        userRepository.save(user);

        return ResponseEntity.ok(user);
    }

    /**
     * Endpoint to lock or unlock a user's account. Only ADMINISTRATOR can lock/unlock accounts.
     */
    @PutMapping("/access")
    public ResponseEntity<Map<String, String>> changeUserAccess(@RequestBody Map<String, String> accessRequest) {
        String username = accessRequest.get("username");
        String operation = accessRequest.get("operation");

        Optional<User> optionalUser = userRepository.findByUsernameIgnoreCase(username);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        User user = optionalUser.get();
        if (user.getRole() == UserRole.ADMINISTRATOR) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        if (operation.equalsIgnoreCase("LOCK")) {
            user.setAccountNonLocked(false);
        } else if (operation.equalsIgnoreCase("UNLOCK")) {
            user.setAccountNonLocked(true);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        userRepository.save(user);

        Map<String, String> response = new HashMap<>();
        response.put("status", "User " + username + " " + (user.isAccountNonLocked() ? "unlocked" : "locked") + "!");
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint to delete a user. Only ADMINISTRATOR can delete users.
     */
    @DeleteMapping("/user/{username}")
    public ResponseEntity<Map<String, String>> deleteUser(@PathVariable String username) {
        Optional<User> optionalUser = userRepository.findByUsernameIgnoreCase(username);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        userRepository.delete(optionalUser.get());

        // Prepare a response with a message confirming the deletion
        Map<String, String> response = new HashMap<>();
        response.put("status", "Deleted successfully!");
        response.put("username", username);

        // Return 200 OK with the response as JSON
        return ResponseEntity.ok(response);
    }

}

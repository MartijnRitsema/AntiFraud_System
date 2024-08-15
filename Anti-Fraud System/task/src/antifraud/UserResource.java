package antifraud;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class UserResource {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserResource(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

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

        // Encode the password and save the user
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User createdUser = userRepository.save(user);

        // Return the created user with a 201 status and the location of the new resource
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }


    @GetMapping("/list")
    public ResponseEntity<List<User>> findUsers() {
        List<User> users = userRepository.findAllByOrderByIdAsc();
        return ResponseEntity.ok(users);
    }

    @DeleteMapping("user/{username}")
    public ResponseEntity<?> deleteUser(@PathVariable String username) {
        Optional<User> user = userRepository.findByUsernameIgnoreCase(username);
        if (user.isPresent()) {
            userRepository.delete(user.get());
            return ResponseEntity.ok(Map.of("username", username, "status", "Deleted successfully!"));
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
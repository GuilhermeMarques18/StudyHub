package dev.guilherme.demo.auth;

import dev.guilherme.demo.security.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import dev.guilherme.demo.user.UserDTO;
import dev.guilherme.demo.user.UserModel;
import dev.guilherme.demo.user.UserResponseDTO;
import dev.guilherme.demo.user.UserService;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenService tokenService;

    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> register(@RequestBody UserDTO userDTO) {
        UserModel savedUser = userService.saveUser(userDTO);
        return ResponseEntity.ok(toDto(savedUser));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody AuthDTO dto) {
        try {
            Authentication authenticate = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(dto.email(), dto.password())
            );

            UserModel user = (UserModel) authenticate.getPrincipal();
            String token = tokenService.generateToken(user);
            return ResponseEntity.ok(new AuthResponseDTO(token, "Bearer"));
        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(401).build();
        }
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> me(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof UserModel user)) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(toDto(user));
    }

    private UserResponseDTO toDto(UserModel user) {
        return new UserResponseDTO(user.getId(), user.getName(), user.getEmail(), user.getCreatedDate());
    }
}

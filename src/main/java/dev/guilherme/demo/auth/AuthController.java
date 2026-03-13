package dev.guilherme.demo.auth;


import dev.guilherme.demo.auth.dtos.AuthDTO;
import dev.guilherme.demo.auth.dtos.AuthRefreshDTO;
import dev.guilherme.demo.auth.dtos.AuthResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import dev.guilherme.demo.user.dtos.UserDTO;
import dev.guilherme.demo.user.UserModel;
import dev.guilherme.demo.user.dtos.UserResponseDTO;
import dev.guilherme.demo.user.UserService;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private AuthService authService;

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
            String token = authService.generateAccessToken(user);
            String refreshToken = authService.generateRefreshToken(user);

            return ResponseEntity.ok(new AuthResponseDTO(token, refreshToken, "Bearer"));
        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(401).build();
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponseDTO> refresh(@RequestBody AuthRefreshDTO dto) {
        String refreshToken = dto.refreshToken();
        String username = authService.extractUsername(refreshToken);

        UserModel user = (UserModel) userService.loadUserByUsername(username);

        if (!authService.validateToken(refreshToken, user)) {
            return ResponseEntity.status(401).build();
        }

        String newAccessToken = authService.generateAccessToken(user);
        String newRefreshToken = authService.generateRefreshToken(user);

        return ResponseEntity.ok(
                new AuthResponseDTO(newAccessToken, newRefreshToken, "Bearer")
        );
    }
    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> myProfile(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof UserModel user)) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(toDto(user));
    }

    private UserResponseDTO toDto(UserModel user) {
        return new UserResponseDTO(user.getId(), user.getName(), user.getEmail(), user.getCreatedDate());
    }
}

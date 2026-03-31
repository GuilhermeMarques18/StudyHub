package dev.guilherme.demo.auth;

import dev.guilherme.demo.auth.dtos.AuthDTO;
import dev.guilherme.demo.auth.dtos.AuthRefreshDTO;
import dev.guilherme.demo.auth.dtos.AuthResponseDTO;
import dev.guilherme.demo.auth.dtos.ChangePasswordDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    private final AuthenticationManager authenticationManager;

    private final AuthService authService;

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
        String username = authService.extractUsername(dto.refreshToken());

        UserModel user = (UserModel) userService.loadUserByUsername(username);
        System.out.println("REFRESH TOKEN: " + dto.refreshToken());

        return  ResponseEntity.ok(new AuthResponseDTO(
                authService.generateAccessToken(user),
                authService.generateRefreshToken(user),
                "Bearer"));
    }
    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> myProfile(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof UserModel user)) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(toDto(user));
    }

    @PutMapping("/change-password")
    public ResponseEntity<Void> changePassword(@RequestBody @Valid ChangePasswordDTO dto, Authentication authentication) {

        if (authentication == null || !(authentication.getPrincipal() instanceof UserModel user)) {
            return ResponseEntity.status(401).build();
        }

        authService.changePassword(user, dto);
        return ResponseEntity.noContent().build();
    }

    private UserResponseDTO toDto(UserModel user) {
        return new UserResponseDTO(user.getId(), user.getName(), user.getEmail(), user.getCreatedDate());
    }
}

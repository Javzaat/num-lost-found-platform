package mn.edu.num.lostfound.auth.service;

import mn.edu.num.lostfound.auth.dto.AuthResponse;
import mn.edu.num.lostfound.auth.dto.LoginRequest;
import mn.edu.num.lostfound.auth.dto.RegisterRequest;
import mn.edu.num.lostfound.auth.entity.UserAccount;
import mn.edu.num.lostfound.auth.repository.UserAccountRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AuthService {

    private final UserAccountRepository userAccountRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AuthService(UserAccountRepository userAccountRepository) {
        this.userAccountRepository = userAccountRepository;
    }

    public AuthResponse register(RegisterRequest request) {
        validateRegisterRequest(request);

        if (userAccountRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("This email is already registered.");
        }

        UserAccount user = new UserAccount();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail().toLowerCase().trim());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));

        UserAccount savedUser = userAccountRepository.save(user);

        return toAuthResponse(savedUser);
    }

    public AuthResponse login(LoginRequest request) {
        validateLoginRequest(request);

        UserAccount user = userAccountRepository.findByEmail(request.getEmail().toLowerCase().trim())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password."));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid email or password.");
        }

        return toAuthResponse(user);
    }

    private AuthResponse toAuthResponse(UserAccount user) {
        String demoToken = UUID.randomUUID().toString();

        return new AuthResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getRole(),
                demoToken
        );
    }

    private void validateRegisterRequest(RegisterRequest request) {
        if (request.getFullName() == null || request.getFullName().trim().isEmpty()) {
            throw new IllegalArgumentException("Full name is required.");
        }

        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required.");
        }

        if (request.getPassword() == null || request.getPassword().length() < 4) {
            throw new IllegalArgumentException("Password must be at least 4 characters.");
        }
    }

    private void validateLoginRequest(LoginRequest request) {
        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required.");
        }

        if (request.getPassword() == null || request.getPassword().isEmpty()) {
            throw new IllegalArgumentException("Password is required.");
        }
    }
}

package mn.edu.num.lostfound.auth.controller;

import mn.edu.num.lostfound.auth.dto.AuthResponse;
import mn.edu.num.lostfound.auth.dto.ErrorResponse;
import mn.edu.num.lostfound.auth.dto.LoginRequest;
import mn.edu.num.lostfound.auth.dto.RegisterRequest;
import mn.edu.num.lostfound.auth.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse register(@RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @GetMapping("/ping")
    public String ping() {
        return "Auth Service is running";
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBadRequest(IllegalArgumentException ex) {
        return new ErrorResponse(400, "Bad Request", ex.getMessage());
    }
}

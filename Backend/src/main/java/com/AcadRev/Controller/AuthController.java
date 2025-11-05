package com.AcadRev.Controller;

import com.AcadRev.Dto.LoginRequest;
import com.AcadRev.Dto.LoginResponse;
import com.AcadRev.Dto.SignUpRequest;
import com.AcadRev.Dto.SignUpResponse;
import com.AcadRev.Service.AuthService;
import com.AcadRev.Service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/v1/auth")
@RestController
@RequiredArgsConstructor
public class AuthController {
    private final JwtService jwtService;
    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<SignUpResponse> signUp (@RequestBody SignUpRequest request){
        try{
            SignUpResponse newUser = authService.signUp(request);
            return ResponseEntity.ok(newUser);
        }catch(DuplicateKeyException e){
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login (@RequestBody LoginRequest request){
        LoginResponse authenticatedUser = authService.authenticate(request);
        authenticatedUser.setExpiresIn(jwtService.getExpirationTime());
        return ResponseEntity.ok(authenticatedUser);
    }

}


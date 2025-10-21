package com.AcadRev.Service;

import com.AcadRev.Dto.LoginRequest;
import com.AcadRev.Dto.LoginResponse;
import com.AcadRev.Dto.SignUpRequest;
import com.AcadRev.Dto.SignUpResponse;
import com.AcadRev.Model.Role;
import com.AcadRev.Model.User;
import com.AcadRev.Model.UserType;
import com.AcadRev.Repository.RoleRepository;
import com.AcadRev.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public SignUpResponse signUp(SignUpRequest inputUser) {

        Role role = roleRepository.findByRole(UserType.valueOf(inputUser.getRole()));
        if(role == null){
            role = roleRepository.save(new Role(UserType.valueOf(inputUser.getRole())));
        }


        User user = User.builder()
                .username(inputUser.getUsername())
                .password(passwordEncoder.encode(inputUser.getPassword()))
                .name(inputUser.getName())
                .role(role)
                .build();

        userRepository.save(user);

        String token = jwtService.generateToken(user);

        return SignUpResponse.builder().token(token).role(role).username(user.getUsername()).build();
    }

    public LoginResponse authenticate(LoginRequest input) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        input.getUsername(),
                        input.getPassword()
                )
        );
        User user = userRepository.findByUsername(input.getUsername())
                .orElseThrow();
        String token = jwtService.generateToken(user);

        return LoginResponse.builder()
                .token(token)
                .username(user.getUsername())
                .build();
    }


}

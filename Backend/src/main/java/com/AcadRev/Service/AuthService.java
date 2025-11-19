package com.AcadRev.Service;

import com.AcadRev.Dto.LoginRequest;
import com.AcadRev.Dto.LoginResponse;
import com.AcadRev.Dto.SignUpRequest;
import com.AcadRev.Dto.SignUpResponse;
import com.AcadRev.Exception.ResourceNotFoundException;
import com.AcadRev.Model.CompanyProfile;
import com.AcadRev.Model.Role;
import com.AcadRev.Model.User;
import com.AcadRev.Model.UserType;
import com.AcadRev.Repository.CompanyProfileRepository;
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
    private final CompanyProfileRepository companyProfileRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public SignUpResponse signUp(SignUpRequest inputUser) {

        Role role = roleRepository.findByRole(UserType.valueOf(inputUser.getRole()));
        if (role == null) {
            role = roleRepository.save(new Role(UserType.valueOf(inputUser.getRole())));
        }

        // For internal auditors, set the company profile they selected
        CompanyProfile companyProfile = null;
        if (UserType.valueOf(inputUser.getRole()) == UserType.INTERNAL_AUDITOR && inputUser.getCompanyId() != null) {
            companyProfile = companyProfileRepository.findById(inputUser.getCompanyId())
                    .orElseThrow(() -> new ResourceNotFoundException("Company not found with ID: " + inputUser.getCompanyId()));
        }

        User user = User.builder()
                .username(inputUser.getUsername())
                .password(passwordEncoder.encode(inputUser.getPassword()))
                .name(inputUser.getName())
                .role(role)
                .companyProfile(companyProfile)
                .build();

        userRepository.save(user);

        String token = jwtService.generateToken(user);

        SignUpResponse.UserInfo userInfo = SignUpResponse.UserInfo.builder()
                .id(user.getId().toString())
                .username(user.getUsername())
                .name(user.getName())
                .role(role.getRole().name())
                .companyId(user.getCompanyProfile() != null ? user.getCompanyProfile().getId() : null)
                .build();

        return SignUpResponse.builder()
                .token(token)
                .role(role)
                .username(user.getUsername())
                .user(userInfo)
                .build();
    }

    public LoginResponse authenticate(LoginRequest input) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        input.getUsername(),
                        input.getPassword()));
        User user = userRepository.findByUsername(input.getUsername())
                .orElseThrow();
        String token = jwtService.generateToken(user);

        LoginResponse.UserInfo userInfo = LoginResponse.UserInfo.builder()
                .id(user.getId().toString())
                .username(user.getUsername())
                .name(user.getName())
                .role(user.getRole().getRole().name())
                .companyId(user.getCompanyProfile() != null ? user.getCompanyProfile().getId() : null)
                .build();

        return LoginResponse.builder()
                .token(token)
                .username(user.getUsername())
                .user(userInfo)
                .build();
    }

}

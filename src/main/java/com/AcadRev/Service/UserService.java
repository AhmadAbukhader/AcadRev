package com.AcadRev.Service;

import com.AcadRev.Model.Role;
import com.AcadRev.Model.User;
import com.AcadRev.Model.UserType;
import com.AcadRev.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public List<String> getCompanies () {
        Role role = new Role(UserType.COMPANY_OWNER);
        List<User> companies =  userRepository.findByRole(role);
        return companies.stream().map(
                User::getName
        ).toList();
    }
}

package com.emma.HNG2.service;

import com.emma.HNG2.model.Organisation;
import com.emma.HNG2.model.User;
import com.emma.HNG2.repository.OrganisationRepository;
import com.emma.HNG2.repository.UserRepository;
import com.emma.HNG2.util.JwtUtil;
import com.emma.HNG2.util.UserRequest;
import com.emma.HNG2.util.UserResponse;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private OrganisationRepository organisationRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Transactional
    public UserResponse registerUser(UserRequest userRequest) {
        if (userRepository.existsByEmail(userRequest.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();
        user.setFirstName(userRequest.getFirstName());
        user.setLastName(userRequest.getLastName());
        user.setEmail(userRequest.getEmail());
        user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        user.setPhone(userRequest.getPhone());

        Organisation defaultOrg = new Organisation();
        defaultOrg.setName(user.getFirstName() + "'s Organisation");
        defaultOrg = organisationRepository.save(defaultOrg);

        user.getOrganisations().add(defaultOrg);
        User savedUser = userRepository.save(user);

        defaultOrg.getUsers().add(savedUser);
        organisationRepository.save(defaultOrg);

        String token = jwtUtil.generateToken(savedUser.getEmail());

        return new UserResponse(
                savedUser.getUserId(),
                savedUser.getFirstName(),
                savedUser.getLastName(),
                savedUser.getEmail(),
                savedUser.getPhone(),
                token
        );
    }

//    @Transactional
//    public User registerUser(User user) {
//        if (userRepository.existsByEmail(user.getEmail())) {
//            throw new RuntimeException("Email already exists");
//        }
//
//        user.setPassword(passwordEncoder.encode(user.getPassword()));
//        Organisation defaultOrg = new Organisation();
//        defaultOrg.setName(user.getFirstName() + "'s Organisation");
//        defaultOrg = organisationRepository.save(defaultOrg);
//
//        user.getOrganisations().add(defaultOrg);
//        User savedUser = userRepository.save(user);
//
//        defaultOrg.getUsers().add(savedUser);
//        organisationRepository.save(defaultOrg);
//
//        return savedUser;
//    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> getUserById(String id) {
        return userRepository.findById(id);
    }
}
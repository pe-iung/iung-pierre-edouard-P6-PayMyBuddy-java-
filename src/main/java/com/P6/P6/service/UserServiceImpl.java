package com.P6.P6.service;

import com.P6.P6.DTO.FriendDTO;
import com.P6.P6.DTO.FriendListResponse;
import com.P6.P6.DTO.SignupRequest;
import com.P6.P6.model.UserEntity;
import com.P6.P6.repositories.UserEntityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class UserServiceImpl implements UserService {
    private final BCryptPasswordEncoder passwordEncoder;
    private final UserEntityRepository userRepository;
    private final AccountService accountService;

    private static final double INITIAL_BALANCE = 0.0;

    @Override
    @Transactional
    public UserEntity signupNewUser(SignupRequest signupRequest) {
        // Validate if email already exists
        if (findByEmail(signupRequest.getEmail()).isPresent()) {
            throw new RuntimeException("Email already registered");
        }

        // Create and save new user
        String encodedPassword = passwordEncoder.encode(signupRequest.getPassword());
        UserEntity newUser = new UserEntity(
                signupRequest.getUsername(),
                encodedPassword,
                signupRequest.getEmail()
        );
        newUser = userRepository.save(newUser);

        accountService.createAccount(newUser);

        return newUser;
    }


    @Override
    public void addFriendToUserEntity(String userEntityEmail, String friendEmail) {
        UserEntity user = findByEmail(userEntityEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found with Id: " + userEntityEmail));


        UserEntity friend = findByEmail(friendEmail)
                .orElseThrow(() -> new IllegalArgumentException("Friend not found with email: " + friendEmail));


        if (user.getFriends().contains(friend)) {
            throw new IllegalArgumentException("Friend is already added!");
        }


        user.getFriends().add(friend);
        log.info("new friend added to {} : {}",userEntityEmail, user.getFriends());
        userRepository.save(user);


    }


    @Override
    @Transactional(readOnly = true)
    public FriendListResponse getFriendList(UserEntity currentUser) {
        log.info("Generating friend list for user: {}", currentUser.getUsername());

        UserEntity userWithFriends = userRepository.findWithFriendsById(currentUser.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<FriendDTO> friendDTOs = userWithFriends.getFriends().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        log.info("Friend list generated with {} friends", friendDTOs.size());
        return new FriendListResponse(friendDTOs, friendDTOs.size());
    }

    private FriendDTO convertToDTO(UserEntity friend) {
        FriendDTO dto = new FriendDTO();
        dto.setId(friend.getId());
        dto.setUsername(friend.getUsername());
        dto.setEmail(friend.getEmail());
        return dto;
    }

    @Override
    public Optional<UserEntity> findByEmail(String email){
        return userRepository.findByEmail(email);
    }
}

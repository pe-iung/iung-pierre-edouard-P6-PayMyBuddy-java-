package com.P6.P6.service;

import com.P6.P6.DTO.*;
import com.P6.P6.model.UserEntity;
import com.P6.P6.repositories.UserEntityRepository;
import com.P6.P6.utils.EmailValidation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.apache.logging.log4j.ThreadContext.isEmpty;

@RequiredArgsConstructor
@Service
@Slf4j
public class UserServiceImpl implements UserService {
    private final BCryptPasswordEncoder passwordEncoder;
    private final UserEntityRepository userRepository;
    //private final AccountService accountService;

    private static final double INITIAL_BALANCE = 0.0;

    /**
     * sign-up/add a new user
     * @param signupRequest
     * @return userId
     */
    @Override
    @Transactional
    public Integer signupNewUser(SignupRequest signupRequest) {
        // Validate if email already exists
        if (findByEmail(signupRequest.getEmail()).isPresent()) {
            throw new RuntimeException("Email already registered");
        }

        if ((signupRequest.getPassword()).isEmpty()) {
            throw new RuntimeException("password is empty");
        }

        if ((signupRequest.getEmail()).isEmpty()) {
            throw new RuntimeException("Email is empty");
        }

        if ((signupRequest.getUsername()).isEmpty()) {
            throw new RuntimeException("Username is empty");
        }
        String regexEmailPattern = "^(.+)@(\\S+)$";
        if (!EmailValidation.patternMatches(signupRequest.getEmail(), regexEmailPattern)){
            throw new RuntimeException("invalid Email");
        }


        // Create and save new user
        String encodedPassword = passwordEncoder.encode(signupRequest.getPassword());
        UserEntity newUser = new UserEntity(
                signupRequest.getUsername(),
                encodedPassword,
                signupRequest.getEmail()
        );
        newUser = userRepository.save(newUser);

        //accountService.createAccount(newUser);
        log.info("from user service impl: this user has been saved {} with id {}", newUser, newUser.getId());
        return newUser.getId();
    }


    /**
     * allow a user to add a new friend via his friend email
     * @param userEntityEmail, friendEmail
     * @return void
     */
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



    /**
     * list all friends attached to a user
     * @param currentUser
     * @return List of User (friends)
     */
    @Override
    @Transactional(readOnly = true)
    public List<UserEntity> getFriendList(UserEntity currentUser) {
        log.info("Generating friend list for user: {}", currentUser.getUsername());

        return userRepository.findWithFriendsById(currentUser.getId())
                .orElseThrow(() -> new RuntimeException("User not found"))
                .getFriends();
    }


    @Override
    public Optional<UserEntity> findByEmail(String email){
        return userRepository.findByEmail(email);
    }

    @Override
    public Optional<UserEntity> findById(Integer userId){
        return userRepository.findById(userId);
    }

    /**
     * allow a user to edit his own username, email, password
     * @param currentUserId, userEditProfilRequest
     * @return userId
     */
    @Override
    public Integer editUser(Integer currentUserId, UserEditProfilRequest userEditProfilRequest) {
        UserEntity user = findById(currentUserId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with Id: " + currentUserId));

        String encodedPassword = passwordEncoder.encode(userEditProfilRequest.getPassword());
        user.setUsername(userEditProfilRequest.getUsername());
        user.setEmail(userEditProfilRequest.getEmail());
        user.setPassword(encodedPassword);

        userRepository.save(user);

        return user.getId();

    }
}

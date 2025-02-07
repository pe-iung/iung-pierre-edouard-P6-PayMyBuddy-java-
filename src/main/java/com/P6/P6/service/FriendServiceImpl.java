package com.P6.P6.service;

import com.P6.P6.DTO.FriendDTO;
import com.P6.P6.DTO.FriendListResponse;
import com.P6.P6.model.UserEntity;
import com.P6.P6.repositories.TransactionRepository;
import com.P6.P6.repositories.UserEntityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FriendServiceImpl implements FriendService{

    private final UserEntityRepository userRepository;
    private final TransactionRepository transactionRepository;


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
}

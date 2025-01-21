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
        log.info("a friendList is beeing generated for user {}",currentUser.getUsername());
        List<FriendDTO> friendDTOs = currentUser.getFriends().stream()
                .map(friend -> {
                    FriendDTO dto = new FriendDTO();
                    dto.setId(friend.getId());
                    dto.setUsername(friend.getUsername());
                    dto.setEmail(friend.getEmail());

                    return dto;
                })
                .collect(Collectors.toList());

        log.info("a friendListResponse is sent to controller{} ", friendDTOs);
        return new FriendListResponse(
                friendDTOs,
                friendDTOs.size()
        );
    }
}

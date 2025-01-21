package com.P6.P6.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
class AddFriendControllerTest {

    @Autowired
    private MockMvc mockMvc;


    @Test
    @WithMockUser
    void showAddFriendForm_ShouldReturnAddFriendPage() throws Exception {
        mockMvc.perform(get("/add-friend"))
                .andExpect(status().isOk())
                .andExpect(view().name("add-friend"));
    }

    @Test
    @WithMockUser
    void addFriend_ShouldAddFriend() throws Exception {
        mockMvc.perform(post("/add-friend")
                        .param("friendEmail", "friend@example.com"))
                .andExpect(status().isOk())
                .andExpect(view().name("add-friend"));

    }
}

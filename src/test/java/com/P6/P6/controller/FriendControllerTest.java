package com.P6.P6.controller;

import com.P6.P6.configuration.TestConfig;
import com.P6.P6.configuration.WithMockCustomUser;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestConfig.class)
class FriendControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockCustomUser(email = "test@example.com")
    void getFriendList_Success() throws Exception {
        mockMvc.perform(get("/friends"))
                .andExpect(status().isOk())
                .andExpect(view().name("friends"))
                .andExpect(model().attributeExists("friendList"));
    }

    @Test
    void getFriendList_Unauthenticated_RedirectsToLogin() throws Exception {
        mockMvc.perform(get("/friends"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }
}
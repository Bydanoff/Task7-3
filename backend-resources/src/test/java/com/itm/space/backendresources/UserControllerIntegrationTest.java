package com.itm.space.backendresources;

import com.itm.space.backendresources.api.request.UserRequest;
import com.itm.space.backendresources.api.response.UserResponse;
import com.itm.space.backendresources.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UserControllerIntegrationTest extends BaseIntegrationTest {
    @MockBean
    private UserService userService;

    @Test
    @WithMockUser(roles = "MODERATOR")
    public void testCreateUser() throws Exception {
        UserRequest userRequest = new UserRequest
                ("user", "user@user.com", "user", "user","user");
        mvc.perform(requestWithContent(post("/api/users"), userRequest))
                .andExpect(status().isOk());
        verify(userService).createUser(any(UserRequest.class));
    }

    @Test
    @WithMockUser(roles = "MODERATOR")
    public void testGetUserById() throws Exception {
        UUID userId = UUID.randomUUID();
        UserResponse userResponse = new UserResponse
                ("user","user", "user@user.com", List.of("ROLE_USER"), List.of("GROUP_USER"));
        doReturn(userResponse).when(userService).getUserById(userId);
        mvc.perform(get("/api/users/" + userId))
                .andExpect(status().isOk());
        verify(userService).getUserById(userId);
    }

    @Test
    @WithMockUser(roles = "MODERATOR")
    public void testHello() throws Exception {
        mvc.perform(get("/api/users/hello"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void testHelloByUnauthorized() throws Exception {
        mvc.perform(get("/api/users/hello"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "MODERATOR")
    void testCreateUserByInvalidUserRequest() throws Exception {
        UserRequest invalidRequest = new UserRequest("", "email","","","");
        mvc.perform(requestWithContent(post("/api/users"), invalidRequest))
                .andExpect(status().isBadRequest());
    }
}

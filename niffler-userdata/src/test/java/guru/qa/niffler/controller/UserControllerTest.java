package guru.qa.niffler.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Sql(scripts = "/currentUserShouldBeReturned.sql")
    @Test
    void currentUserShouldBeReturned() throws Exception {
        mockMvc.perform(get("/internal/users/current")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("username", "dima")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("dima"))
                .andExpect(jsonPath("$.fullname").value("Dmitrii Tuchs"))
                .andExpect(jsonPath("$.currency").value("RUB"))
                .andExpect(jsonPath("$.photo").isNotEmpty())
                .andExpect(jsonPath("$.photoSmall").isNotEmpty());
    }

    @Sql(scripts = "/incomeRequestShouldBeAccepted.sql")
    @Test
    void incomeRequestShouldBeAccepted() throws Exception {
        mockMvc.perform(post("/internal/invitations/accept")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("username", "Igor")
                        .param("targetUsername", "dima")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("dima"))
                .andExpect(jsonPath("$.fullname").value("Dmitrii Tuchs"))
                .andExpect(jsonPath("$.currency").value("RUB"))
                .andExpect(jsonPath("$.photo").isNotEmpty())
                .andExpect(jsonPath("$.friendshipStatus").value("FRIEND"))
                .andExpect(jsonPath("$.photoSmall").isNotEmpty());
    }

    @Sql(scripts = "/incomeRequestShouldBeDeclined.sql")
    @Test
    void incomeRequestShouldBeDeclined() throws Exception {
        mockMvc.perform(post("/internal/invitations/decline")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("username", "Igor")
                        .param("targetUsername", "dima")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("dima"))
                .andExpect(jsonPath("$.fullname").value("Dmitrii Tuchs"))
                .andExpect(jsonPath("$.currency").value("RUB"))
                .andExpect(jsonPath("$.photo").isNotEmpty())
                .andExpect(jsonPath("$.photoSmall").isNotEmpty());

        mockMvc.perform(get("/internal/friends/all")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("username", "Igor"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*").isEmpty());
    }

    @Sql(scripts = "/friendshipShouldBeRemoved.sql")
    @Test
    void friendshipShouldBeRemoved() throws Exception {
        mockMvc.perform(delete("/internal/friends/remove")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("username", "Igor")
                        .param("targetUsername", "dima")
                )
                .andExpect(status().isOk());

        mockMvc.perform(get("/internal/friends/all")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("username", "Igor"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*").isEmpty());
    }
}
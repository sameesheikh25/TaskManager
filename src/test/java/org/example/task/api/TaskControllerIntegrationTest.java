package org.example.task.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class TaskControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void create_and_get() throws Exception {
        String createJson = "{\"title\":\"T1\",\"description\":\"D\",\"status\":\"PENDING\",\"dueDate\":\"" + java.time.LocalDate.now().plusDays(2) + "\"}";
        String response = mockMvc.perform(post("/tasks").contentType(MediaType.APPLICATION_JSON).content(createJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andReturn().getResponse().getContentAsString();
        String createdId = response.replaceAll(".*\"id\":\"([^\"]+)\".*", "$1");
        mockMvc.perform(get("/tasks/" + createdId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("T1"));
    }
}

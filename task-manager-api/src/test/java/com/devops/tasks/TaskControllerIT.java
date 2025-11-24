package com.devops.tasks;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TaskControllerIT {

    private static final String API_BASE_URL = "/api/v1/tasks";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TaskService taskService;

    @BeforeEach
    void beforeEach() {
        // clear in-memory repository
        taskService.getAllTasks().forEach(t -> taskService.deleteTask(t.getId()));
    }

    @AfterEach
    void afterEach() {
        taskService.getAllTasks().forEach(t -> taskService.deleteTask(t.getId()));
    }

    @Test
    void createTaskShouldReturnCreatedTask() throws Exception {
        Task payload = new Task();
        payload.setTitle("CI Create Test");
        payload.setDescription("created by integration test");

        String body = objectMapper.writeValueAsString(payload);

        mockMvc.perform(post(API_BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.title").value("CI Create Test"));
    }

    @Test
    void getAllTasksShouldReturnTasks() throws Exception {
        // create two tasks via service
        Task t1 = new Task();
        t1.setTitle("T1");
        t1.setDescription("d1");
        taskService.createTask(t1, "u1");
        Task t2 = new Task();
        t2.setTitle("T2");
        t2.setDescription("d2");
        taskService.createTask(t2, "u2");

        String result = mockMvc.perform(get(API_BASE_URL))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        Task[] tasks = objectMapper.readValue(result, Task[].class);
        assertThat(tasks).hasSizeGreaterThanOrEqualTo(2);
    }

    @Test
    void getTaskByIdShouldReturnTask() throws Exception {
        Task t = new Task(); t.setTitle("FindMe"); t.setDescription("desc");
        Task created = taskService.createTask(t, "creator");

        mockMvc.perform(get(API_BASE_URL + "/{id}", created.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(created.getId()))
                .andExpect(jsonPath("$.title").value("FindMe"));
    }

    @Test
    void updateTaskShouldReturnUpdated() throws Exception {
        Task t = new Task(); t.setTitle("Old"); t.setDescription("old");
        Task created = taskService.createTask(t, "creator");

        Task update = new Task(); update.setTitle("New Title"); update.setDescription("new desc");
        String body = objectMapper.writeValueAsString(update);

        mockMvc.perform(put(API_BASE_URL + "/{id}", created.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("New Title"));
    }

    @Test
    void deleteTaskShouldRemoveAndReturnNoContent() throws Exception {
        Task t = new Task(); t.setTitle("ToDelete"); Task created = taskService.createTask(t, "creator");

        mockMvc.perform(delete(API_BASE_URL + "/{id}", created.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get(API_BASE_URL + "/{id}", created.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteTasksByUserShouldRemoveAllAssignedToUser() throws Exception {
        Task a = new Task();
        a.setTitle("A");
        a.setAssignedToUserId("userX");
        taskService.createTask(a, "creator");
        Task b = new Task();
        b.setTitle("B");
        b.setAssignedToUserId("userX");
        taskService.createTask(b, "creator");
        Task c = new Task();
        c.setTitle("C");
        c.setAssignedToUserId("other");
        taskService.createTask(c, "creator");

        mockMvc.perform(delete(API_BASE_URL + "/users/{userId}", "userX"))
                .andExpect(status().isNoContent());

        List<Task> remaining = taskService.getAllTasks();
        assertThat(remaining).allMatch(tk -> !"userX".equals(tk.getAssignedToUserId()));
    }

}

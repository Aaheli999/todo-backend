package com.example.todo_api.controller;

import com.example.todo_api.entity.Todo;
import com.example.todo_api.repository.TodoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class TodoControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TodoRepository todoRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        todoRepository.deleteAll();
    }

    @Test
    void testCreateTodo() throws Exception {
        Todo todo = new Todo("Test Todo", "Description", false, LocalDateTime.now());

        mockMvc.perform(post("/todos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(todo)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title", is("Test Todo")))
                .andExpect(jsonPath("$.description", is("Description")))
                .andExpect(jsonPath("$.completed", is(false)));
    }

    @Test
    void testGetAllTodos() throws Exception {
        Todo todo1 = new Todo("Todo 1", "Description 1", false, LocalDateTime.now());
        Todo todo2 = new Todo("Todo 2", "Description 2", true, LocalDateTime.now());
        todoRepository.save(todo1);
        todoRepository.save(todo2);

        mockMvc.perform(get("/todos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].title", is("Todo 1")))
                .andExpect(jsonPath("$[1].title", is("Todo 2")));
    }

    @Test
    void testGetTodoById() throws Exception {
        Todo todo = new Todo("Test Todo", "Description", false, LocalDateTime.now());
        todo = todoRepository.save(todo);

        mockMvc.perform(get("/todos/" + todo.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("Test Todo")))
                .andExpect(jsonPath("$.description", is("Description")));
    }

    @Test
    void testUpdateTodo() throws Exception {
        Todo todo = new Todo("Old Title", "Old Description", false, LocalDateTime.now());
        todo = todoRepository.save(todo);

        Todo updatedTodo = new Todo("Updated Title", "Updated Description", true, LocalDateTime.now());

        mockMvc.perform(put("/todos/" + todo.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedTodo)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("Updated Title")))
                .andExpect(jsonPath("$.description", is("Updated Description")))
                .andExpect(jsonPath("$.completed", is(true)));
    }

    @Test
    void testMarkTodoAsComplete() throws Exception {
        Todo todo = new Todo("Test Todo", "Description", false, LocalDateTime.now());
        todo = todoRepository.save(todo);

        mockMvc.perform(patch("/todos/" + todo.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.completed", is(true)));
    }

    @Test
    void testDeleteTodo() throws Exception {
        Todo todo = new Todo("Test Todo", "Description", false, LocalDateTime.now());
        todo = todoRepository.save(todo);

        mockMvc.perform(delete("/todos/" + todo.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/todos/" + todo.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetTodoCount() throws Exception {
        Todo todo1 = new Todo("Todo 1", "Description 1", false, LocalDateTime.now());
        Todo todo2 = new Todo("Todo 2", "Description 2", true, LocalDateTime.now());
        todoRepository.save(todo1);
        todoRepository.save(todo2);

        mockMvc.perform(get("/todos/count"))
                .andExpect(status().isOk())
                .andExpect(content().string("2"));
    }
}
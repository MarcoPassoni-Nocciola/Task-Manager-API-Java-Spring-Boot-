package com.devops.tasks;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the TaskService class.
 * This ensures the core business logic (CRUD operations) works correctly.
 * * NOTE: Since TaskService uses an in-memory map (ConcurrentHashMap), 
 * we need to initialize a fresh instance before each test.
 */
class TaskServiceTest {

    private TaskService taskService;
    private final String TEST_USER_ID = "testUser123";

    /**
     * Set up a fresh TaskService instance before every test.
     */
    @BeforeEach
    void setUp() {
        // Initialize the service (it will contain the 3 dummy tasks defined in its constructor)
        taskService = new TaskService();
    }

    @Test
    void testGetAllTasksInitialCount() {
        // Verifica che ci siano 3 task iniziali (dummy data)
        List<Task> tasks = taskService.getAllTasks();
        assertEquals(3, tasks.size(), "Dovrebbe esserci un conteggio iniziale di 3 task.");
    }

    @Test
    void testCreateAndRetrieveTask() {
        // Crea un nuovo task
        Task newTask = new Task("Pipeline Review", "Check GitLab stages.", TEST_USER_ID);
        Task createdTask = taskService.createTask(newTask, TEST_USER_ID);

        // Verifica che il task sia stato creato con un ID
        assertNotNull(createdTask.getId(), "Il task creato dovrebbe avere un ID assegnato.");
        
        // Cerca il task per ID
        Optional<Task> retrievedTask = taskService.getTaskById(createdTask.getId());

        // Verifica che il task sia stato trovato e che i dati siano corretti
        assertTrue(retrievedTask.isPresent(), "Il task appena creato dovrebbe essere recuperabile.");
        assertEquals("Pipeline Review", retrievedTask.get().getTitle(), "Il titolo del task dovrebbe corrispondere.");
        assertEquals(Task.Status.TODO, retrievedTask.get().getStatus(), "Lo stato di default dovrebbe essere TODO.");
    }

    @Test
    void testUpdateTaskStatus() {
        // 1. Trova un ID esistente (il primo task nella dummy data)
        String taskIdToUpdate = taskService.getAllTasks().get(0).getId();

        // 2. Crea un oggetto task con il nuovo stato
        Task updateDetails = new Task();
        updateDetails.setStatus(Task.Status.DONE); // Imposta il nuovo stato

        // 3. Esegui l'aggiornamento
        Optional<Task> updatedTask = taskService.updateTask(taskIdToUpdate, updateDetails);

        // 4. Verifica
        assertTrue(updatedTask.isPresent(), "L'aggiornamento dovrebbe avere successo.");
        assertEquals(Task.Status.DONE, updatedTask.get().getStatus(), "Lo stato dovrebbe essere aggiornato a DONE.");
    }

    @Test
    void testDeleteTask() {
        // 1. Crea un task appositamente da eliminare
        Task taskToDelete = new Task("Ephemeral Task", "Should be deleted.", TEST_USER_ID);
        Task createdTask = taskService.createTask(taskToDelete, TEST_USER_ID);

        // 2. Esegui la cancellazione
        boolean isDeleted = taskService.deleteTask(createdTask.getId());

        // 3. Verifica l'eliminazione
        assertTrue(isDeleted, "La cancellazione del task dovrebbe restituire true.");
        
        // 4. Verifica che non sia più recuperabile
        Optional<Task> retrievedTask = taskService.getTaskById(createdTask.getId());
        assertFalse(retrievedTask.isPresent(), "Il task eliminato non dovrebbe più esistere.");
    }

    @Test
    void testGetTasksAssignedToUser() {
        // Recupera i task assegnati a 'userA' (dovrebbe essercene uno dal costruttore)
        List<Task> userATasks = taskService.getTasksAssignedToUser("userA");
        assertEquals(1, userATasks.size(), "Dovrebbe esserci 1 task assegnato a 'userA' all'inizio.");
        
        // Crea un nuovo task assegnato a 'userA'
        Task newTaskForA = new Task("Test New Task", "For user A", "anotherUser");
        newTaskForA.setAssignedToUserId("userA");
        taskService.createTask(newTaskForA, "anotherUser");

        // Verifica che ora ci siano due task per 'userA'
        List<Task> userATasksAfter = taskService.getTasksAssignedToUser("userA");
        assertEquals(2, userATasksAfter.size(), "Dovrebbero esserci 2 task assegnati a 'userA' dopo la creazione.");
    }
}



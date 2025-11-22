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

    @Test
    void testDeleteTasksOfUser_DeletesAllTasksForUser() {
        // Setup: Create multiple tasks assigned to the same user
        String targetUserId = "targetUser";
        Task task1 = new Task("Task 1", "First task", "creator1");
        task1.setAssignedToUserId(targetUserId);
        Task created1 = taskService.createTask(task1, "creator1");
        
        Task task2 = new Task("Task 2", "Second task", "creator2");
        task2.setAssignedToUserId(targetUserId);
        Task created2 = taskService.createTask(task2, "creator2");
        
        Task task3 = new Task("Task 3", "Third task", "creator3");
        task3.setAssignedToUserId(targetUserId);
        Task created3 = taskService.createTask(task3, "creator3");
        
        // Create a task assigned to a different user (should NOT be deleted)
        Task otherTask = new Task("Other Task", "Different user", "creator4");
        otherTask.setAssignedToUserId("otherUser");
        Task otherCreated = taskService.createTask(otherTask, "creator4");
        
        // Verify initial state: 3 tasks for targetUser + 1 for otherUser + 3 from constructor = 7 total
        List<Task> targetUserTasksBefore = taskService.getTasksAssignedToUser(targetUserId);
        assertEquals(3, targetUserTasksBefore.size(), "Dovrebbero esserci 3 task assegnati a targetUser prima della cancellazione.");
        
        // Execute: Delete all tasks for targetUser
        boolean deleted = taskService.deleteTasksOfUser(targetUserId);
        
        // Verify: Method returns true
        assertTrue(deleted, "deleteTasksOfUser dovrebbe restituire true quando vengono eliminati task.");
        
        // Verify: All tasks for targetUser are deleted
        List<Task> targetUserTasksAfter = taskService.getTasksAssignedToUser(targetUserId);
        assertEquals(0, targetUserTasksAfter.size(), "Tutti i task assegnati a targetUser dovrebbero essere eliminati.");
        
        // Verify: Tasks cannot be retrieved by ID
        assertFalse(taskService.getTaskById(created1.getId()).isPresent(), "Task 1 dovrebbe essere eliminato.");
        assertFalse(taskService.getTaskById(created2.getId()).isPresent(), "Task 2 dovrebbe essere eliminato.");
        assertFalse(taskService.getTaskById(created3.getId()).isPresent(), "Task 3 dovrebbe essere eliminato.");
        
        // Verify: Task assigned to other user is NOT deleted
        assertTrue(taskService.getTaskById(otherCreated.getId()).isPresent(), "Il task assegnato ad altri utenti NON dovrebbe essere eliminato.");
        List<Task> otherUserTasks = taskService.getTasksAssignedToUser("otherUser");
        assertEquals(1, otherUserTasks.size(), "Il task dell'altro utente dovrebbe ancora esistere.");
    }

    @Test
    void testDeleteTasksOfUser_ReturnsFalseWhenNoTasksFound() {
        // Try to delete tasks for a user that has no tasks
        String nonExistentUser = "nonExistentUser";
        
        // Verify user has no tasks
        List<Task> tasksBefore = taskService.getTasksAssignedToUser(nonExistentUser);
        assertEquals(0, tasksBefore.size(), "L'utente non dovrebbe avere task assegnati.");
        
        // Execute: Try to delete
        boolean deleted = taskService.deleteTasksOfUser(nonExistentUser);
        
        // Verify: Method returns false
        assertFalse(deleted, "deleteTasksOfUser dovrebbe restituire false quando non ci sono task da eliminare.");
    }

    @Test
    void testDeleteTasksOfUser_DeletesSingleTask() {
        // Setup: Create one task for a user
        String singleUser = "singleUser";
        Task task = new Task("Single Task", "Only task", "creator");
        task.setAssignedToUserId(singleUser);
        Task created = taskService.createTask(task, "creator");
        
        // Verify initial state
        List<Task> tasksBefore = taskService.getTasksAssignedToUser(singleUser);
        assertEquals(1, tasksBefore.size(), "Dovrebbe esserci 1 task prima della cancellazione.");
        
        // Execute: Delete
        boolean deleted = taskService.deleteTasksOfUser(singleUser);
        
        // Verify: Returns true and task is deleted
        assertTrue(deleted, "Dovrebbe restituire true quando viene eliminato almeno un task.");
        assertFalse(taskService.getTaskById(created.getId()).isPresent(), "Il task dovrebbe essere eliminato.");
        List<Task> tasksAfter = taskService.getTasksAssignedToUser(singleUser);
        assertEquals(0, tasksAfter.size(), "Non dovrebbero esserci più task dopo la cancellazione.");
    }

    @Test
    void testDeleteTasksOfUser_DoesNotAffectOtherUsers() {
        // Setup: Create tasks for multiple users
        String user1 = "user1";
        String user2 = "user2";
        String user3 = "user3";
        
        Task task1 = new Task("User1 Task", "Task for user1", "creator");
        task1.setAssignedToUserId(user1);
        Task created1 = taskService.createTask(task1, "creator");
        
        Task task2 = new Task("User2 Task", "Task for user2", "creator");
        task2.setAssignedToUserId(user2);
        Task created2 = taskService.createTask(task2, "creator");
        
        Task task3 = new Task("User3 Task", "Task for user3", "creator");
        task3.setAssignedToUserId(user3);
        Task created3 = taskService.createTask(task3, "creator");
        
        // Execute: Delete only user1's tasks
        boolean deleted = taskService.deleteTasksOfUser(user1);
        
        // Verify: user1's task is deleted
        assertTrue(deleted, "Dovrebbe restituire true.");
        assertFalse(taskService.getTaskById(created1.getId()).isPresent(), "Il task di user1 dovrebbe essere eliminato.");
        
        // Verify: Other users' tasks are NOT deleted
        assertTrue(taskService.getTaskById(created2.getId()).isPresent(), "Il task di user2 NON dovrebbe essere eliminato.");
        assertTrue(taskService.getTaskById(created3.getId()).isPresent(), "Il task di user3 NON dovrebbe essere eliminato.");
        
        List<Task> user2Tasks = taskService.getTasksAssignedToUser(user2);
        assertEquals(1, user2Tasks.size(), "user2 dovrebbe ancora avere il suo task.");
        
        List<Task> user3Tasks = taskService.getTasksAssignedToUser(user3);
        assertEquals(1, user3Tasks.size(), "user3 dovrebbe ancora avere il suo task.");
    }
}



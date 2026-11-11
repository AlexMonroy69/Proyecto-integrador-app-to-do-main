package co.Proyecto_Backendll.service;

import co.Proyecto_Backendll.api.Dtos.PageResponse;
import co.Proyecto_Backendll.api.Dtos.TaskResponse;
import co.Proyecto_Backendll.common.ApiException;
import co.Proyecto_Backendll.store.AppUser;
import co.Proyecto_Backendll.store.Task;
import co.Proyecto_Backendll.store.TaskRepository;
import co.Proyecto_Backendll.store.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

/** Todas las operaciones se limitan a las tareas del usuario autenticado. */
@Service
@Transactional
public class TaskService {

    private static final Set<String> SORTS = Set.of("createdAt", "title", "completed");

    private final TaskRepository tasks;
    private final UserRepository users;

    public TaskService(TaskRepository tasks, UserRepository users) {
        this.tasks = tasks;
        this.users = users;
    }

    @Transactional(readOnly = true)
    public PageResponse<TaskResponse> list(Long ownerId, String q, Boolean completed,
                                           int page, int size, String sort, String dir) {
        PageRequest pageable = PageSupport.pageRequest(page, size, sort, dir, SORTS);
        String pattern = PageSupport.pattern(q);
        Page<Task> result = completed == null
                ? tasks.search(ownerId, pattern, pageable)
                : tasks.searchByCompleted(ownerId, pattern, completed, pageable);
        return PageSupport.toResponse(result, TaskResponse::from);
    }

    @Transactional(readOnly = true)
    public TaskResponse get(Long ownerId, Long id) {
        return TaskResponse.from(find(ownerId, id));
    }

    public TaskResponse create(Long ownerId, String title, String description) {
        AppUser owner = users.findById(ownerId).orElseThrow(ApiException::unauthorizedEmpty);
        // Al crear, completed siempre se fuerza a false.
        Task task = new Task(title.trim(), clean(description), owner);
        return TaskResponse.from(tasks.save(task));
    }

    public TaskResponse update(Long ownerId, Long id, String title, String description, boolean completed) {
        Task task = find(ownerId, id);
        task.setTitle(title.trim());
        task.setDescription(clean(description));
        task.setCompleted(completed);
        return TaskResponse.from(tasks.save(task));
    }

    public TaskResponse complete(Long ownerId, Long id) {
        Task task = find(ownerId, id);
        task.setCompleted(true);
        return TaskResponse.from(tasks.save(task));
    }

    public void delete(Long ownerId, Long id) {
        tasks.delete(find(ownerId, id));
    }

    private Task find(Long ownerId, Long id) {
        return tasks.findByIdAndOwnerId(id, ownerId)
                .orElseThrow(() -> ApiException.notFound("Tarea no encontrada"));
    }

    private static String clean(String description) {
        if (description == null || description.isBlank()) {
            return null;
        }
        return description.trim();
    }
}

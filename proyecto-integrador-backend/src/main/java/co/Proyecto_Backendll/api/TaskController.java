package co.Proyecto_Backendll.api;

import co.Proyecto_Backendll.api.Dtos.PageResponse;
import co.Proyecto_Backendll.api.Dtos.TaskRequest;
import co.Proyecto_Backendll.api.Dtos.TaskResponse;
import co.Proyecto_Backendll.security.AuthPrincipal;
import co.Proyecto_Backendll.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService tasks;

    public TaskController(TaskService tasks) {
        this.tasks = tasks;
    }

    @GetMapping
    public PageResponse<TaskResponse> list(@AuthenticationPrincipal AuthPrincipal principal,
                                           @RequestParam(defaultValue = "0") int page,
                                           @RequestParam(defaultValue = "20") int size,
                                           @RequestParam(required = false) String q,
                                           @RequestParam(required = false) Boolean completed,
                                           @RequestParam(defaultValue = "createdAt") String sort,
                                           @RequestParam(defaultValue = "desc") String dir) {
        return tasks.list(principal.userId(), q, completed, page, size, sort, dir);
    }

    @GetMapping("/{id}")
    public TaskResponse get(@AuthenticationPrincipal AuthPrincipal principal, @PathVariable Long id) {
        return tasks.get(principal.userId(), id);
    }

    @PostMapping
    public ResponseEntity<TaskResponse> create(@AuthenticationPrincipal AuthPrincipal principal,
                                               @Valid @RequestBody TaskRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(tasks.create(principal.userId(), req.getTitle(), req.getDescription()));
    }

    @PutMapping("/{id}")
    public TaskResponse update(@AuthenticationPrincipal AuthPrincipal principal, @PathVariable Long id,
                               @Valid @RequestBody TaskRequest req) {
        return tasks.update(principal.userId(), id, req.getTitle(), req.getDescription(), req.getCompleted());
    }

    @PatchMapping("/{id}/complete")
    public TaskResponse complete(@AuthenticationPrincipal AuthPrincipal principal, @PathVariable Long id) {
        return tasks.complete(principal.userId(), id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@AuthenticationPrincipal AuthPrincipal principal, @PathVariable Long id) {
        tasks.delete(principal.userId(), id);
        return ResponseEntity.noContent().build();
    }
}

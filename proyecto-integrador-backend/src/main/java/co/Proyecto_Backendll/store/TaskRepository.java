package co.Proyecto_Backendll.store;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, Long> {

    Optional<Task> findByIdAndOwnerId(Long id, Long ownerId);

    boolean existsByOwnerId(Long ownerId);

    @Query(value = "select t from Task t where t.owner.id = :ownerId "
            + "and (lower(t.title) like lower(:pattern) or lower(coalesce(t.description, '')) like lower(:pattern))",
            countQuery = "select count(t) from Task t where t.owner.id = :ownerId "
            + "and (lower(t.title) like lower(:pattern) or lower(coalesce(t.description, '')) like lower(:pattern))")
    Page<Task> search(@Param("ownerId") Long ownerId, @Param("pattern") String pattern, Pageable pageable);

    @Query(value = "select t from Task t where t.owner.id = :ownerId and t.completed = :completed "
            + "and (lower(t.title) like lower(:pattern) or lower(coalesce(t.description, '')) like lower(:pattern))",
            countQuery = "select count(t) from Task t where t.owner.id = :ownerId and t.completed = :completed "
            + "and (lower(t.title) like lower(:pattern) or lower(coalesce(t.description, '')) like lower(:pattern))")
    Page<Task> searchByCompleted(@Param("ownerId") Long ownerId, @Param("pattern") String pattern,
                                 @Param("completed") boolean completed, Pageable pageable);
}

package co.Proyecto_Backendll.domain.repository;

import co.Proyecto_Backendll.domain.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    Optional<Course> findByName(String name);

    boolean existsByCode(String code);

    boolean existsByCodeAndIdNot(String code, Long id);
}

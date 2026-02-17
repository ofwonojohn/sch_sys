package primaryschool.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import primaryschool.model.Teacher;
import primaryschool.model.Classroom;
import primaryschool.model.Subject;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Long> {

    List<Teacher> findByClassrooms(Classroom classroom);

    List<Teacher> findBySubjects(Subject subject);
}

package primaryschool.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import primaryschool.model.Student;
import primaryschool.model.Classroom;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    List<Student> findByClassroom(Classroom classroom);

}

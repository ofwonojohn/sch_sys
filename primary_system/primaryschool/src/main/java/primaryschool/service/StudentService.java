package primaryschool.service;

import java.util.List;

import org.springframework.stereotype.Service;

import primaryschool.model.Student;
import primaryschool.model.Classroom;
import primaryschool.repository.StudentRepository;

@Service
public class StudentService {

    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public List<Student> getStudentsByClassroom(Classroom classroom) {
        return studentRepository.findByClassroom(classroom);
    }

    public List<Student> getStudentsByClassroomId(Long classroomId) {
        return studentRepository.findByClassroomId(classroomId);
    }

    public Student getStudentById(Long id) {
        return studentRepository.findById(id).orElse(null);
    }

    public Student saveStudent(Student student) {
        return studentRepository.save(student);
    }
}

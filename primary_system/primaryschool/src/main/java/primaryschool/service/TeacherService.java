package primaryschool.service;

import java.util.List;

import org.springframework.stereotype.Service;

import primaryschool.model.Teacher;
import primaryschool.model.Classroom;
import primaryschool.model.Subject;
import primaryschool.repository.TeacherRepository;

@Service
public class TeacherService {

    private final TeacherRepository teacherRepository;

    public TeacherService(TeacherRepository teacherRepository) {
        this.teacherRepository = teacherRepository;
    }

    public List<Teacher> getAllTeachers() {
        return teacherRepository.findAll();
    }

    public Teacher saveTeacher(Teacher teacher) {
        return teacherRepository.save(teacher);
    }

    public List<Teacher> getTeachersByClass(Classroom classroom) {
        return teacherRepository.findByClassrooms(classroom);
    }

    public List<Teacher> getTeachersBySubject(Subject subject) {
        return teacherRepository.findBySubjects(subject);
    }

    public Teacher getTeacherById(Long id) {
        return teacherRepository.findById(id).orElse(null);
    }
}

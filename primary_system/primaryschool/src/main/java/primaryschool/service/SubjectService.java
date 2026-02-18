package primaryschool.service;

import primaryschool.model.Subject;
import primaryschool.model.Classroom;
import primaryschool.repository.SubjectRepository;
import primaryschool.repository.ClassroomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class SubjectService {

    @Autowired
    private SubjectRepository subjectRepository;
    
    @Autowired
    private ClassroomRepository classroomRepository;

    public List<Subject> getAllSubjects() {
        return subjectRepository.findAll();
    }

    public List<Subject> getSubjectsByClassroom(Long classroomId) {
        // Get the classroom and return its subjects
        Classroom classroom = classroomRepository.findById(classroomId).orElse(null);
        if (classroom != null) {
            return classroom.getSubjects();
        }
        return subjectRepository.findAll();
    }

    public Subject saveSubject(Subject subject) {
        return subjectRepository.save(subject);
    }

    public Subject getSubjectById(Long id) {
        return subjectRepository.findById(id).orElse(null);
    }
}

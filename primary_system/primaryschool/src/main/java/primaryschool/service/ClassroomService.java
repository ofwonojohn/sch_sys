package primaryschool.service;

import primaryschool.model.Classroom;
import primaryschool.repository.ClassroomRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ClassroomService {

    private final ClassroomRepository classroomRepository;

    public ClassroomService(ClassroomRepository classroomRepository) {
        this.classroomRepository = classroomRepository;
    }

    public List<Classroom> getAllClasses() {
        return classroomRepository.findAll();
    }

    public Classroom saveClassroom(Classroom classroom) {
        return classroomRepository.save(classroom);
    }

    public Classroom getClassroomById(Long id) {
    return classroomRepository.findById(id).orElse(null);
    }

}

package primaryschool.service;

import primaryschool.model.Subject;
import primaryschool.repository.SubjectRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class SubjectService {

    private final SubjectRepository subjectRepository;

    public SubjectService(SubjectRepository subjectRepository) {
        this.subjectRepository = subjectRepository;
    }

    public List<Subject> getAllSubjects() {
        return subjectRepository.findAll();
    }

    public Subject saveSubject(Subject subject) {
        return subjectRepository.save(subject);
    }
}

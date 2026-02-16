package primaryschool.config;

import primaryschool.model.Classroom;
import primaryschool.model.Subject;
import primaryschool.repository.ClassroomRepository;
import primaryschool.repository.SubjectRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.List;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initData(ClassroomRepository classRepo, SubjectRepository subjRepo) {
        return args -> {

            // Create subjects
            Subject literacy1 = new Subject(); literacy1.setName("Literacy 1"); subjRepo.save(literacy1);
            Subject literacy2 = new Subject(); literacy2.setName("Literacy 2"); subjRepo.save(literacy2);
            Subject reading1 = new Subject(); reading1.setName("Reading 1"); subjRepo.save(reading1);
            Subject pe = new Subject(); pe.setName("P.E"); subjRepo.save(pe);
            Subject math = new Subject(); math.setName("Mathematics"); subjRepo.save(math);
            Subject social = new Subject(); social.setName("Social Studies"); subjRepo.save(social);
            Subject english = new Subject(); english.setName("English"); subjRepo.save(english);
            Subject science = new Subject(); science.setName("Science"); subjRepo.save(science);

            // Create classes P.1-P.3
            Classroom p1 = new Classroom(); p1.setName("P.1"); p1.setSubjects(List.of(literacy1, literacy2, reading1, pe));
            classRepo.save(p1);
            Classroom p2 = new Classroom(); p2.setName("P.2"); p2.setSubjects(List.of(literacy1, literacy2, reading1, pe));
            classRepo.save(p2);
            Classroom p3 = new Classroom(); p3.setName("P.3"); p3.setSubjects(List.of(literacy1, literacy2, reading1, pe));
            classRepo.save(p3);

            // Create classes P.4-P.7
            Classroom p4 = new Classroom(); p4.setName("P.4"); p4.setSubjects(List.of(social, math, science, english));
            classRepo.save(p4);
            Classroom p5 = new Classroom(); p5.setName("P.5"); p5.setSubjects(List.of(social, math, science, english));
            classRepo.save(p5);
            Classroom p6 = new Classroom(); p6.setName("P.6"); p6.setSubjects(List.of(social, math, science, english));
            classRepo.save(p6);
            Classroom p7 = new Classroom(); p7.setName("P.7"); p7.setSubjects(List.of(social, math, science, english));
            classRepo.save(p7);
        };
    }
}

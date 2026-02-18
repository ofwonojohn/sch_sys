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

            // Check if data already exists to prevent duplicates
            if (!classRepo.findAll().isEmpty()) {
                return;
            }

            // Create Uganda Primary School subjects
            // Lower primary (P.1-P.3)
            Subject literacy1 = new Subject(); literacy1.setName("Literacy 1"); subjRepo.save(literacy1);
            Subject literacy2 = new Subject(); literacy2.setName("Literacy 2"); subjRepo.save(literacy2);
            Subject reading = new Subject(); reading.setName("Reading"); subjRepo.save(reading);
            Subject writing = new Subject(); writing.setName("Writing"); subjRepo.save(writing);
            Subject oral = new Subject(); oral.setName("Oral Literature"); subjRepo.save(oral);
            Subject math = new Subject(); math.setName("Mathematics"); subjRepo.save(math);
            Subject environmental = new Subject(); environmental.setName("Environmental Science"); subjRepo.save(environmental);
            Subject religious = new Subject(); religious.setName("Religious Education"); subjRepo.save(religious);
            Subject art = new Subject(); art.setName("Art & Craft"); subjRepo.save(art);
            Subject pe = new Subject(); pe.setName("Physical Education"); subjRepo.save(pe);
            Subject music = new Subject(); music.setName("Music"); subjRepo.save(music);
            
            // Upper primary (P.4-P.7)
            Subject english = new Subject(); english.setName("English"); subjRepo.save(english);
            Subject mathematics = new Subject(); mathematics.setName("Mathematics"); subjRepo.save(mathematics);
            Subject science = new Subject(); science.setName("Science"); subjRepo.save(science);
            Subject social = new Subject(); social.setName("Social Studies"); subjRepo.save(social);
            Subject re = new Subject(); re.setName("Religious Education"); subjRepo.save(re);
            Subject agriculture = new Subject(); agriculture.setName("Agriculture"); subjRepo.save(agriculture);
            
            // Create classes P.1-P.7
            Classroom p1 = new Classroom(); p1.setName("P.1"); p1.setSubjects(List.of(literacy1, reading, writing, math, environmental, religious, art, pe, music));
            classRepo.save(p1);
            Classroom p2 = new Classroom(); p2.setName("P.2"); p2.setSubjects(List.of(literacy1, literacy2, reading, writing, math, environmental, religious, art, pe, music));
            classRepo.save(p2);
            Classroom p3 = new Classroom(); p3.setName("P.3"); p3.setSubjects(List.of(literacy1, literacy2, reading, writing, oral, math, environmental, religious, art, pe, music));
            classRepo.save(p3);

            Classroom p4 = new Classroom(); p4.setName("P.4"); p4.setSubjects(List.of(english, mathematics, science, social, re, agriculture, art, pe));
            classRepo.save(p4);
            Classroom p5 = new Classroom(); p5.setName("P.5"); p5.setSubjects(List.of(english, mathematics, science, social, re, agriculture, art, pe));
            classRepo.save(p5);
            Classroom p6 = new Classroom(); p6.setName("P.6"); p6.setSubjects(List.of(english, mathematics, science, social, re, agriculture, art, pe));
            classRepo.save(p6);
            Classroom p7 = new Classroom(); p7.setName("P.7"); p7.setSubjects(List.of(english, mathematics, science, social, re, agriculture, art, pe));
            classRepo.save(p7);
        };
    }
}

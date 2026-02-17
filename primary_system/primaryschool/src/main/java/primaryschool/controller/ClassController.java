package primaryschool.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import primaryschool.model.Classroom;
import primaryschool.model.Student;
import primaryschool.service.ClassroomService;
import primaryschool.service.StudentService;

@Controller
public class ClassController {

    private final ClassroomService classroomService;
    private final StudentService studentService;

    public ClassController(ClassroomService classroomService,
                           StudentService studentService) {
        this.classroomService = classroomService;
        this.studentService = studentService;
    }

    @GetMapping("/classes")
    public String viewClasses(@RequestParam(required = false) Long classId,
                              Model model) {

        List<Classroom> classes = classroomService.getAllClasses();
        model.addAttribute("classes", classes);

        if (classId != null) {
            Classroom selectedClass = classroomService.getClassroomById(classId);
            List<Student> students = studentService.getStudentsByClassroom(selectedClass);

            model.addAttribute("selectedClass", selectedClass);
            model.addAttribute("students", students);
        }

        return "classes";
    }
}

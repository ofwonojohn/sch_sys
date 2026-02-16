package primaryschool.controller;

import primaryschool.model.Classroom;
import primaryschool.model.Student;
import primaryschool.service.StudentService;
import primaryschool.service.ClassroomService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/students")
public class StudentController {

    private final StudentService studentService;
    private final ClassroomService classroomService;

    public StudentController(StudentService studentService, ClassroomService classroomService) {
        this.studentService = studentService;
        this.classroomService = classroomService;
    }

    @GetMapping
    public String listStudents(Model model) {
        model.addAttribute("students", studentService.getAllStudents());
        return "students";
    }

    @GetMapping("/new")
    public String createStudentForm(Model model) {
        model.addAttribute("student", new Student());
        model.addAttribute("classes", classroomService.getAllClasses());
        return "student-form";
    }

    @PostMapping
    public String saveStudent(@ModelAttribute Student student) {
        if (student.getClassroom() != null && student.getClassroom().getId() != null) {
            // Fetch classroom from DB
            Classroom classroom = classroomService.getAllClasses()
                .stream()
                .filter(c -> c.getId().equals(student.getClassroom().getId()))
                .findFirst()
                .orElse(null);
            student.setClassroom(classroom);
        }

        studentService.saveStudent(student);
        return "redirect:/students";
    }


}

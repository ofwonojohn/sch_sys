package primaryschool.controller;

import primaryschool.model.Classroom;
import primaryschool.model.Student;
import primaryschool.service.StudentService;
import primaryschool.service.ClassroomService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/students")
public class StudentController {

    private final StudentService studentService;
    private final ClassroomService classroomService;

    public StudentController(StudentService studentService, ClassroomService classroomService) {
        this.studentService = studentService;
        this.classroomService = classroomService;
    }

    // ================== LIST + FILTER ==================
    @GetMapping
    public String listStudents(
            @RequestParam(required = false) Long classId,
            Model model) {

        List<Student> students;

        if (classId != null) {
            Classroom classroom = classroomService.getAllClasses()
                    .stream()
                    .filter(c -> c.getId().equals(classId))
                    .findFirst()
                    .orElse(null);

            if (classroom != null) {
                students = studentService.getStudentsByClassroom(classroom);
            } else {
                students = studentService.getAllStudents();
            }
        } else {
            students = studentService.getAllStudents();
        }

        model.addAttribute("students", students);
        model.addAttribute("classes", classroomService.getAllClasses());
        model.addAttribute("selectedClassId", classId);

        return "students";
    }

    // ================== CREATE ==================
    @GetMapping("/new")
    public String createStudentForm(Model model) {
        model.addAttribute("student", new Student());
        model.addAttribute("classes", classroomService.getAllClasses());
        return "student-form";
    }

    @PostMapping
    public String saveStudent(@ModelAttribute Student student) {

        if (student.getClassroom() != null && student.getClassroom().getId() != null) {

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

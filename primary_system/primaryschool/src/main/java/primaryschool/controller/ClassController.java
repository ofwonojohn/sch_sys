package primaryschool.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import primaryschool.model.Classroom;
import primaryschool.service.ClassroomService;
import primaryschool.service.StudentService;

@Controller
@RequestMapping("/classes")
public class ClassController {

    @Autowired
    private ClassroomService classroomService;

    @Autowired
    private StudentService studentService;

    @GetMapping
    public String viewClasses(
            @RequestParam(required = false) Long classId,
            Model model) {

        model.addAttribute("classes", classroomService.getAllClasses());

        if (classId != null) {
            Classroom classroom = classroomService.getClassroomById(classId);

            model.addAttribute("selectedClass", classroom);
            model.addAttribute("students", studentService.getStudentsByClassroom(classroom));
        }

        return "classes";
    }
}

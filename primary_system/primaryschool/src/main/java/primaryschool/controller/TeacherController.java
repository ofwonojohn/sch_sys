package primaryschool.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import primaryschool.service.TeacherService;

@Controller
@RequestMapping("/teachers")
public class TeacherController {

    private final TeacherService teacherService;

    public TeacherController(TeacherService teacherService) {
        this.teacherService = teacherService;
    }

    @GetMapping
    public String teachersDashboard(Model model) {

        model.addAttribute("teachers", teacherService.getAllTeachers());

        return "teachers-dashboard";
    }
}

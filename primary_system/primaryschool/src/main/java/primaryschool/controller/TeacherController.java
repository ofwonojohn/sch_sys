package primaryschool.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import primaryschool.model.Teacher;
import primaryschool.model.Classroom;
import primaryschool.model.Subject;
import primaryschool.service.TeacherService;
import primaryschool.service.ClassroomService;
import primaryschool.service.SubjectService;

import java.util.List;

@Controller
@RequestMapping("/teachers")
public class TeacherController {

    private final TeacherService teacherService;
    private final ClassroomService classroomService;
    private final SubjectService subjectService;

    public TeacherController(TeacherService teacherService,
                             ClassroomService classroomService,
                             SubjectService subjectService) {
        this.teacherService = teacherService;
        this.classroomService = classroomService;
        this.subjectService = subjectService;
    }

    @GetMapping
    public String listTeachers(Model model) {
        model.addAttribute("teachers", teacherService.getAllTeachers());
        model.addAttribute("classes", classroomService.getAllClasses());
        model.addAttribute("subjects", subjectService.getAllSubjects());
        return "teachers";
    }

    @GetMapping("/new")
    public String createTeacherForm(Model model) {
        model.addAttribute("teacher", new Teacher());
        model.addAttribute("classes", classroomService.getAllClasses());
        model.addAttribute("subjects", subjectService.getAllSubjects());
        return "teacher-form";
    }

    @PostMapping
    public String saveTeacher(@ModelAttribute Teacher teacher) {
        teacherService.saveTeacher(teacher);
        return "redirect:/teachers";
    }

    @GetMapping("/filter")
    public String filterTeachers(@RequestParam(required = false) Long classId,
                                 @RequestParam(required = false) Long subjectId,
                                 Model model) {

        List<Teacher> teachers = null;

        if (classId != null) {
            Classroom classroom = classroomService.getClassroomById(classId);
            teachers = teacherService.getTeachersByClass(classroom);
        }

        if (subjectId != null) {
            Subject subject = subjectService.getSubjectById(subjectId);
            teachers = teacherService.getTeachersBySubject(subject);
        }

        model.addAttribute("teachers", teachers);
        model.addAttribute("classes", classroomService.getAllClasses());
        model.addAttribute("subjects", subjectService.getAllSubjects());

        return "teachers";
    }
}

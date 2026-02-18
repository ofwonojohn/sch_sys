package primaryschool.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import primaryschool.model.Teacher;
import primaryschool.model.Classroom;
import primaryschool.model.Subject;
import primaryschool.service.TeacherService;
import primaryschool.service.SubjectService;
import primaryschool.service.ClassroomService;

import java.util.List;

@Controller
@RequestMapping("/teachers")
public class TeacherController {

    private final TeacherService teacherService;
    private final SubjectService subjectService;
    private final ClassroomService classroomService;

    public TeacherController(TeacherService teacherService, SubjectService subjectService, ClassroomService classroomService) {
        this.teacherService = teacherService;
        this.subjectService = subjectService;
        this.classroomService = classroomService;
    }

    @GetMapping
    public String teachersDashboard(Model model) {

        model.addAttribute("teachers", teacherService.getAllTeachers());

        return "teachers-dashboard";
    }

    @GetMapping("/new")
    public String showTeacherForm(Model model) {
        Teacher teacher = new Teacher();
        List<Subject> subjects = subjectService.getAllSubjects();
        List<Classroom> classrooms = classroomService.getAllClasses();
        
        model.addAttribute("teacher", teacher);
        model.addAttribute("subjects", subjects);
        model.addAttribute("classrooms", classrooms);
        
        return "teacher-form";
    }

    @PostMapping("/save")
    public String saveTeacher(@ModelAttribute Teacher teacher, @RequestParam(required = false) Long classTeacher) {
        // Handle class teacher assignment
        if (classTeacher != null) {
            Classroom classroom = classroomService.getClassroomById(classTeacher);
            if (classroom != null) {
                classroom.setClassTeacher(teacher);
                // Note: This will be saved when we save the teacher
            }
        }
        
        teacherService.saveTeacher(teacher);
        
        // Update classroom's class teacher if selected
        if (classTeacher != null) {
            Classroom classroom = classroomService.getClassroomById(classTeacher);
            if (classroom != null) {
                classroom.setClassTeacher(teacher);
                classroomService.saveClassroom(classroom);
            }
        }
        
        return "redirect:/teachers";
    }
}

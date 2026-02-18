package primaryschool.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import primaryschool.model.Classroom;
import primaryschool.model.Performance;
import primaryschool.model.Student;
import primaryschool.model.Subject;
import primaryschool.service.ClassroomService;
import primaryschool.service.PerformanceService;
import primaryschool.service.StudentService;
import primaryschool.service.SubjectService;

import java.time.Year;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/performance")
public class PerformanceController {

    @Autowired
    private PerformanceService performanceService;

    @Autowired
    private ClassroomService classroomService;

    @Autowired
    private StudentService studentService;

    @Autowired
    private SubjectService subjectService;

    // ===============================
    // Performance Dashboard - Select Class
    // ===============================

    @GetMapping("/dashboard")
    public String performanceDashboard(Model model) {
        List<Classroom> classrooms = classroomService.getAllClassrooms();
        model.addAttribute("classrooms", classrooms);
        return "performance/performance-dashboard";
    }

    // ===============================
    // View Class Performance
    // ===============================

    @GetMapping("/class/{classroomId}")
    public String viewClassPerformance(
            @PathVariable Long classroomId,
            @RequestParam(required = false) Integer term,
            @RequestParam(required = false) Integer year,
            Model model) {

        Classroom classroom = classroomService.getClassroomById(classroomId);
        model.addAttribute("classroom", classroom);

        // Default to current term and year if not provided
        if (term == null) {
            term = 1;
        }
        if (year == null) {
            year = Year.now().getValue();
        }

        // Get available terms and years
        List<Integer> availableTerms = Arrays.asList(1, 2, 3);
        List<Integer> availableYears = new ArrayList<>();
        int currentYear = Year.now().getValue();
        for (int y = currentYear; y >= currentYear - 5; y--) {
            availableYears.add(y);
        }

        model.addAttribute("availableTerms", availableTerms);
        model.addAttribute("availableYears", availableYears);
        model.addAttribute("selectedTerm", term);
        model.addAttribute("selectedYear", year);

        // Calculate results
        List<PerformanceService.StudentResult> results = 
                performanceService.calculateClassResults(classroomId, term, year);
        
        model.addAttribute("results", results);
        
        // Get top 3 performers
        List<PerformanceService.StudentResult> top3 = 
                performanceService.getTopPerformers(classroomId, term, year);
        model.addAttribute("top3", top3);

        // Get subject analysis
        List<PerformanceService.SubjectAnalysis> subjectAnalysis = 
                performanceService.getSubjectAnalysis(classroomId, term, year);
        model.addAttribute("subjectAnalysis", subjectAnalysis);

        return "performance/class-performance";
    }

    // ===============================
    // Enter Marks Form
    // ===============================

    @GetMapping("/enter-marks/{classroomId}")
    public String enterMarksForm(
            @PathVariable Long classroomId,
            @RequestParam(required = false) Integer term,
            @RequestParam(required = false) Integer year,
            Model model) {

        Classroom classroom = classroomService.getClassroomById(classroomId);
        model.addAttribute("classroom", classroom);

        // Default values
        if (term == null) term = 1;
        if (year == null) year = Year.now().getValue();

        // Get students in the class
        List<Student> students = studentService.getStudentsByClassroomId(classroomId);
        
        // Get subjects for the class
        List<Subject> subjects = subjectService.getSubjectsByClassroom(classroomId);
        
        // If no subjects assigned, get all subjects
        if (subjects.isEmpty()) {
            subjects = subjectService.getAllSubjects();
        }

        List<Integer> availableTerms = Arrays.asList(1, 2, 3);
        List<Integer> availableYears = new ArrayList<>();
        int currentYear = Year.now().getValue();
        for (int y = currentYear; y >= currentYear - 5; y--) {
            availableYears.add(y);
        }

        model.addAttribute("students", students);
        model.addAttribute("subjects", subjects);
        model.addAttribute("availableTerms", availableTerms);
        model.addAttribute("availableYears", availableYears);
        model.addAttribute("selectedTerm", term);
        model.addAttribute("selectedYear", year);

        return "performance/enter-marks";
    }

    // ===============================
    // Save Marks
    // ===============================

    @PostMapping("/save-marks")
    public String saveMarks(
            @RequestParam Long classroomId,
            @RequestParam Long studentId,
            @RequestParam Long subjectId,
            @RequestParam Integer term,
            @RequestParam Integer year,
            @RequestParam Double marks,
            Model model) {

        try {
            // Check for duplicate
            boolean isDuplicate = performanceService.isDuplicateEntry(studentId, subjectId, term, year);
            
            if (isDuplicate) {
                // Update existing
                performanceService.savePerformance(studentId, subjectId, classroomId, term, year, marks);
            } else {
                // Save new
                performanceService.savePerformance(studentId, subjectId, classroomId, term, year, marks);
            }
            
            model.addAttribute("success", "Marks saved successfully!");
        } catch (Exception e) {
            model.addAttribute("error", "Error saving marks: " + e.getMessage());
        }

        return "redirect:/performance/enter-marks/" + classroomId + "?term=" + term + "&year=" + year;
    }

    // ===============================
    // Batch Save Marks
    // ===============================

    @PostMapping("/save-batch-marks")
    public String saveBatchMarks(
            @RequestParam Long classroomId,
            @RequestParam Integer term,
            @RequestParam Integer year,
            @RequestParam Long subjectId,
            @RequestParam Map<String, String> allParams,
            Model model) {

        try {
            // Get all students in the class
            List<Student> students = studentService.getStudentsByClassroomId(classroomId);
            
            for (Student student : students) {
                String paramName = "marks_" + student.getId();
                if (allParams.containsKey(paramName)) {
                    String marksStr = allParams.get(paramName);
                    if (marksStr != null && !marksStr.trim().isEmpty()) {
                        try {
                            Double marks = Double.parseDouble(marksStr);
                            if (marks >= 0 && marks <= 100) {
                                performanceService.savePerformance(
                                        student.getId(), subjectId, classroomId, term, year, marks);
                            }
                        } catch (NumberFormatException e) {
                            // Skip invalid marks
                        }
                    }
                }
            }
            
            model.addAttribute("success", "All marks saved successfully!");
        } catch (Exception e) {
            model.addAttribute("error", "Error saving marks: " + e.getMessage());
        }

        return "redirect:/performance/enter-marks/" + classroomId + "?term=" + term + "&year=" + year;
    }

    // ===============================
    // Edit Marks Form
    // ===============================

    @GetMapping("/edit/{performanceId}")
    public String editMarksForm(@PathVariable Long performanceId, Model model) {
        Optional<Performance> performance = performanceService.getPerformanceById(performanceId);
        
        if (performance.isPresent()) {
            model.addAttribute("performance", performance.get());
            return "performance/edit-marks";
        }
        
        return "redirect:/performance/dashboard?error=Performance not found";
    }

    // ===============================
    // Update Marks
    // ===============================

    @PostMapping("/update-marks")
    public String updateMarks(
            @RequestParam Long performanceId,
            @RequestParam Double marks,
            Model model) {

        try {
            Optional<Performance> perfOpt = performanceService.getPerformanceById(performanceId);
            if (perfOpt.isPresent()) {
                Performance p = perfOpt.get();
                performanceService.savePerformance(
                        p.getStudent().getId(),
                        p.getSubject().getId(),
                        p.getClassroom().getId(),
                        p.getTerm(),
                        p.getYear(),
                        marks);
                
                model.addAttribute("success", "Marks updated successfully!");
                return "redirect:/performance/class/" + p.getClassroom().getId() + 
                       "?term=" + p.getTerm() + "&year=" + p.getYear();
            }
        } catch (Exception e) {
            model.addAttribute("error", "Error updating marks: " + e.getMessage());
        }

        return "redirect:/performance/dashboard?error=Error updating marks";
    }

    // ===============================
    // Subject Analysis View
    // ===============================

    @GetMapping("/subject-analysis/{classroomId}")
    public String subjectAnalysis(
            @PathVariable Long classroomId,
            @RequestParam(required = false) Integer term,
            @RequestParam(required = false) Integer year,
            Model model) {

        Classroom classroom = classroomService.getClassroomById(classroomId);
        model.addAttribute("classroom", classroom);

        if (term == null) term = 1;
        if (year == null) year = Year.now().getValue();

        List<Integer> availableTerms = Arrays.asList(1, 2, 3);
        List<Integer> availableYears = new ArrayList<>();
        int currentYear = Year.now().getValue();
        for (int y = currentYear; y >= currentYear - 5; y--) {
            availableYears.add(y);
        }

        model.addAttribute("availableTerms", availableTerms);
        model.addAttribute("availableYears", availableYears);
        model.addAttribute("selectedTerm", term);
        model.addAttribute("selectedYear", year);

        List<PerformanceService.SubjectAnalysis> analysis = 
                performanceService.getSubjectAnalysis(classroomId, term, year);
        model.addAttribute("analysis", analysis);

        return "performance/subject-analysis";
    }

    // ===============================
    // Student Report Card
    // ===============================

    @GetMapping("/student-report/{studentId}")
    public String studentReport(
            @PathVariable Long studentId,
            @RequestParam(required = false) Integer term,
            @RequestParam(required = false) Integer year,
            Model model) {

        Student student = studentService.getStudentById(studentId);
        if (student == null) {
            return "redirect:/performance/dashboard?error=Student not found";
        }
        model.addAttribute("student", student);

        if (term == null) term = 1;
        if (year == null) year = Year.now().getValue();

        // Get the student's performances
        List<Performance> performances = performanceService.getStudentPerformances(studentId, term, year);
        model.addAttribute("performances", performances);

        // Calculate summary
        double total = performances.stream().mapToDouble(Performance::getMarks).sum();
        double average = performances.isEmpty() ? 0 : total / performances.size();
        
        // Calculate aggregate (best 4)
        List<Integer> gradePoints = performances.stream()
                .map(Performance::getGradePoints)
                .sorted()
                .collect(Collectors.toList());
        
        int aggregate = 0;
        int count = Math.min(4, gradePoints.size());
        for (int i = 0; i < count; i++) {
            aggregate += gradePoints.get(i);
        }
        
        model.addAttribute("totalMarks", total);
        model.addAttribute("averageScore", Math.round(average * 100.0) / 100.0);
        model.addAttribute("aggregate", aggregate);
        model.addAttribute("division", Performance.calculateDivision(aggregate));
        model.addAttribute("selectedTerm", term);
        model.addAttribute("selectedYear", year);

        List<Integer> availableTerms = Arrays.asList(1, 2, 3);
        List<Integer> availableYears = new ArrayList<>();
        int currentYear = Year.now().getValue();
        for (int y = currentYear; y >= currentYear - 5; y--) {
            availableYears.add(y);
        }
        model.addAttribute("availableTerms", availableTerms);
        model.addAttribute("availableYears", availableYears);

        return "performance/student-report";
    }
}

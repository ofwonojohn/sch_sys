package primaryschool.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import primaryschool.model.Classroom;
import primaryschool.model.Performance;
import primaryschool.model.Student;
import primaryschool.model.Subject;
import primaryschool.repository.PerformanceRepository;
import primaryschool.repository.StudentRepository;
import primaryschool.repository.SubjectRepository;
import primaryschool.repository.ClassroomRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class PerformanceService {

    @Autowired
    private PerformanceRepository performanceRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private ClassroomRepository classroomRepository;

    // ===============================
    // Save or Update Performance
    // ===============================

    @Transactional
    public Performance savePerformance(Long studentId, Long subjectId, Long classroomId, 
                                       Integer term, Integer year, Double marks) {
        
        // Check for existing performance
        Optional<Performance> existing = performanceRepository
                .findByStudentIdAndSubjectIdAndTermAndYear(studentId, subjectId, term, year);

        if (existing.isPresent()) {
            // Update existing
            Performance p = existing.get();
            p.setMarks(marks);
            return performanceRepository.save(p);
        } else {
            // Create new
            Student student = studentRepository.findById(studentId)
                    .orElseThrow(() -> new RuntimeException("Student not found"));
            Subject subject = subjectRepository.findById(subjectId)
                    .orElseThrow(() -> new RuntimeException("Subject not found"));
            Classroom classroom = classroomRepository.findById(classroomId)
                    .orElseThrow(() -> new RuntimeException("Classroom not found"));

            Performance performance = new Performance(student, subject, classroom, term, year, marks);
            return performanceRepository.save(performance);
        }
    }

    // ===============================
    // Check for Duplicate Entry
    // ===============================

    public boolean isDuplicateEntry(Long studentId, Long subjectId, Integer term, Integer year) {
        return performanceRepository.existsByStudentIdAndSubjectIdAndTermAndYear(
                studentId, subjectId, term, year);
    }

    // ===============================
    // Get All Performances for a Class
    // ===============================

    public List<Performance> getClassPerformances(Long classroomId, Integer term, Integer year) {
        return performanceRepository.findByClassroomIdAndTermAndYear(classroomId, term, year);
    }

    // ===============================
    // Get Student Performance
    // ===============================

    public List<Performance> getStudentPerformances(Long studentId, Integer term, Integer year) {
        return performanceRepository.findByStudentIdAndTermAndYear(studentId, term, year);
    }

    // ===============================
    // Student Result DTO
    // ===============================

    public static class StudentResult {
        private Student student;
        private Map<String, Double> subjectMarks = new LinkedHashMap<>();
        private Map<String, String> subjectGrades = new LinkedHashMap<>();
        private Double totalMarks;
        private Double averageScore;
        private Integer aggregate; // Best 4 subjects
        private String division;
        private Integer position;
        private List<String> subjectsList = new ArrayList<>();

        // Getters and Setters
        public Student getStudent() { return student; }
        public void setStudent(Student student) { this.student = student; }
        public Map<String, Double> getSubjectMarks() { return subjectMarks; }
        public void setSubjectMarks(Map<String, Double> subjectMarks) { this.subjectMarks = subjectMarks; }
        public Map<String, String> getSubjectGrades() { return subjectGrades; }
        public void setSubjectGrades(Map<String, String> subjectGrades) { this.subjectGrades = subjectGrades; }
        public Double getTotalMarks() { return totalMarks; }
        public void setTotalMarks(Double totalMarks) { this.totalMarks = totalMarks; }
        public Double getAverageScore() { return averageScore; }
        public void setAverageScore(Double averageScore) { this.averageScore = averageScore; }
        public Integer getAggregate() { return aggregate; }
        public void setAggregate(Integer aggregate) { this.aggregate = aggregate; }
        public String getDivision() { return division; }
        public void setDivision(String division) { this.division = division; }
        public Integer getPosition() { return position; }
        public void setPosition(Integer position) { this.position = position; }
        public List<String> getSubjectsList() { return subjectsList; }
        public void setSubjectsList(List<String> subjectsList) { this.subjectsList = subjectsList; }
    }

    // ===============================
    // Calculate Student Results with Rankings
    // ===============================

    public List<StudentResult> calculateClassResults(Long classroomId, Integer term, Integer year) {
        List<Performance> performances = performanceRepository.findByClassroomTermYear(classroomId, term, year);
        
        // Group by student
        Map<Student, List<Performance>> studentPerformances = performances.stream()
                .collect(Collectors.groupingBy(Performance::getStudent));

        List<StudentResult> results = new ArrayList<>();

        for (Map.Entry<Student, List<Performance>> entry : studentPerformances.entrySet()) {
            StudentResult result = new StudentResult();
            result.setStudent(entry.getKey());

            List<Performance> perfs = entry.getValue();
            
            // Calculate total marks
            double total = 0;
            for (Performance p : perfs) {
                result.getSubjectMarks().put(p.getSubject().getName(), p.getMarks());
                result.getSubjectGrades().put(p.getSubject().getName(), p.getGrade());
                result.getSubjectsList().add(p.getSubject().getName());
                total += p.getMarks();
            }
            result.setTotalMarks(total);

            // Calculate average
            double avg = perfs.isEmpty() ? 0 : total / perfs.size();
            result.setAverageScore(Math.round(avg * 100.0) / 100.0);

            // Calculate aggregate (best 4 subjects by grade points - lower is better)
            List<Integer> gradePoints = perfs.stream()
                    .map(Performance::getGradePoints)
                    .sorted()
                    .collect(Collectors.toList());

            int aggregate = 0;
            int count = Math.min(4, gradePoints.size());
            for (int i = 0; i < count; i++) {
                aggregate += gradePoints.get(i);
            }
            result.setAggregate(aggregate);

            // Calculate division
            result.setDivision(Performance.calculateDivision(aggregate));

            results.add(result);
        }

        // Sort by total marks descending for ranking
        results.sort((a, b) -> b.getTotalMarks().compareTo(a.getTotalMarks()));

        // Assign positions
        int position = 1;
        for (StudentResult r : results) {
            r.setPosition(position++);
        }

        return results;
    }

    // ===============================
    // Get Top 3 Performers
    // ===============================

    public List<StudentResult> getTopPerformers(Long classroomId, Integer term, Integer year) {
        List<StudentResult> results = calculateClassResults(classroomId, term, year);
        return results.stream().limit(3).collect(Collectors.toList());
    }

    // ===============================
    // Subject Performance Analysis
    // ===============================

    public static class SubjectAnalysis {
        private String subjectName;
        private Double averageMarks;
        private Double highestMarks;
        private Double lowestMarks;
        private Long totalStudents;
        private Map<String, Long> gradeDistribution = new HashMap<>();

        // Getters and Setters
        public String getSubjectName() { return subjectName; }
        public void setSubjectName(String subjectName) { this.subjectName = subjectName; }
        public Double getAverageMarks() { return averageMarks; }
        public void setAverageMarks(Double averageMarks) { this.averageMarks = averageMarks; }
        public Double getHighestMarks() { return highestMarks; }
        public void setHighestMarks(Double highestMarks) { this.highestMarks = highestMarks; }
        public Double getLowestMarks() { return lowestMarks; }
        public void setLowestMarks(Double lowestMarks) { this.lowestMarks = lowestMarks; }
        public Long getTotalStudents() { return totalStudents; }
        public void setTotalStudents(Long totalStudents) { this.totalStudents = totalStudents; }
        public Map<String, Long> getGradeDistribution() { return gradeDistribution; }
        public void setGradeDistribution(Map<String, Long> gradeDistribution) { this.gradeDistribution = gradeDistribution; }
    }

    public List<SubjectAnalysis> getSubjectAnalysis(Long classroomId, Integer term, Integer year) {
        List<Performance> performances = performanceRepository.findByClassroomTermYear(classroomId, term, year);
        
        // Group by subject
        Map<Subject, List<Performance>> subjectPerformances = performances.stream()
                .collect(Collectors.groupingBy(Performance::getSubject));

        List<SubjectAnalysis> analyses = new ArrayList<>();

        for (Map.Entry<Subject, List<Performance>> entry : subjectPerformances.entrySet()) {
            SubjectAnalysis analysis = new SubjectAnalysis();
            analysis.setSubjectName(entry.getKey().getName());

            List<Performance> perfs = entry.getValue();
            
            // Calculate average
            double avg = perfs.stream().mapToDouble(Performance::getMarks).average().orElse(0);
            analysis.setAverageMarks(Math.round(avg * 100.0) / 100.0);

            // Calculate highest
            analysis.setHighestMarks(perfs.stream().mapToDouble(Performance::getMarks).max().orElse(0));

            // Calculate lowest
            analysis.setLowestMarks(perfs.stream().mapToDouble(Performance::getMarks).min().orElse(0));

            // Total students
            analysis.setTotalStudents((long) perfs.size());

            // Grade distribution
            Map<String, Long> dist = perfs.stream()
                    .collect(Collectors.groupingBy(Performance::getGrade, Collectors.counting()));
            analysis.setGradeDistribution(dist);

            analyses.add(analysis);
        }

        // Sort by average descending
        analyses.sort((a, b) -> b.getAverageMarks().compareTo(a.getAverageMarks()));

        return analyses;
    }

    // ===============================
    // Get Available Terms and Years
    // ===============================

    public List<Integer> getAvailableTerms(Long classroomId) {
        return performanceRepository.findDistinctTermsByClassroomId(classroomId);
    }

    public List<Integer> getAvailableYears(Long classroomId) {
        return performanceRepository.findDistinctYearsByClassroomId(classroomId);
    }

    // ===============================
    // Get Performance by ID
    // ===============================

    public Optional<Performance> getPerformanceById(Long id) {
        return performanceRepository.findById(id);
    }

    // ===============================
    // Delete Performance
    // ===============================

    @Transactional
    public void deletePerformance(Long id) {
        performanceRepository.deleteById(id);
    }
}

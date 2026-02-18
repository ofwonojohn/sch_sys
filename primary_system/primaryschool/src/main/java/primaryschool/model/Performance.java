package primaryschool.model;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
public class Performance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @ManyToOne
    @JoinColumn(name = "classroom_id", nullable = false)
    private Classroom classroom;

    @Column(nullable = false)
    private Integer term; // 1, 2, 3, or 4

    @Column(nullable = false)
    private Integer year;

    @Column(nullable = false)
    private Double marks; // Score out of 100

    // ===============================
    // Calculated Fields (stored for performance)
    // ===============================
    private String grade; // 1-7 based on Uganda grading system
    private Integer gradePoints; // For aggregate calculation

    // ===============================
    // Constructors
    // ===============================

    public Performance() {
    }

    public Performance(Student student, Subject subject, Classroom classroom, Integer term, Integer year, Double marks) {
        this.student = student;
        this.subject = subject;
        this.classroom = classroom;
        this.term = term;
        this.year = year;
        this.marks = marks;
        calculateGrade();
    }

    // ===============================
    // Uganda Primary School Grading System
    // Grade 1: 80-100 (Distinction)
    // Grade 2: 70-79 (Very Good)
    // Grade 3: 60-69 (Good)
    // Grade 4: 50-59 (Satisfactory)
    // Grade 5: 40-49 (Fail)
    // Grade 6: 30-39 (Bad Fail)
    // Grade 7: 0-29 (Fail)
    // ===============================

    public void calculateGrade() {
        if (marks >= 80) {
            this.grade = "1";
            this.gradePoints = 1;
        } else if (marks >= 70) {
            this.grade = "2";
            this.gradePoints = 2;
        } else if (marks >= 60) {
            this.grade = "3";
            this.gradePoints = 3;
        } else if (marks >= 50) {
            this.grade = "4";
            this.gradePoints = 4;
        } else if (marks >= 40) {
            this.grade = "5";
            this.gradePoints = 5;
        } else if (marks >= 30) {
            this.grade = "6";
            this.gradePoints = 6;
        } else {
            this.grade = "7";
            this.gradePoints = 7;
        }
    }

    // ===============================
    // Uganda Division System (based on aggregate of best 4 subjects)
    // Division I: Aggregate 4-12
    // Division II: Aggregate 13-23
    // Division III: Aggregate 24-33
    // Division IV: Aggregate 34-40
    // ===============================

    public static String calculateDivision(int aggregate) {
        if (aggregate >= 4 && aggregate <= 12) {
            return "I";
        } else if (aggregate >= 13 && aggregate <= 23) {
            return "II";
        } else if (aggregate >= 24 && aggregate <= 33) {
            return "III";
        } else if (aggregate >= 34 && aggregate <= 40) {
            return "IV";
        } else {
            return "U"; // Ungraded/Fail
        }
    }

    // ===============================
    // Getters and Setters
    // ===============================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public Classroom getClassroom() {
        return classroom;
    }

    public void setClassroom(Classroom classroom) {
        this.classroom = classroom;
    }

    public Integer getTerm() {
        return term;
    }

    public void setTerm(Integer term) {
        this.term = term;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Double getMarks() {
        return marks;
    }

    public void setMarks(Double marks) {
        this.marks = marks;
        calculateGrade();
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public Integer getGradePoints() {
        return gradePoints;
    }

    public void setGradePoints(Integer gradePoints) {
        this.gradePoints = gradePoints;
    }

    // ===============================
    // Equals and HashCode (based on composite key)
    // ===============================

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Performance that = (Performance) o;
        return Objects.equals(student, that.student) &&
               Objects.equals(subject, that.subject) &&
               Objects.equals(term, that.term) &&
               Objects.equals(year, that.year);
    }

    @Override
    public int hashCode() {
        return Objects.hash(student, subject, term, year);
    }
}

package primaryschool.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import primaryschool.model.Performance;

import java.util.List;
import java.util.Optional;

@Repository
public interface PerformanceRepository extends JpaRepository<Performance, Long> {

    // Find performance by student, subject, term, and year
    Optional<Performance> findByStudentIdAndSubjectIdAndTermAndYear(
            Long studentId, Long subjectId, Integer term, Integer year);

    // Check if performance exists for duplicate prevention
    boolean existsByStudentIdAndSubjectIdAndTermAndYear(
            Long studentId, Long subjectId, Integer term, Integer year);

    // Get all performances for a class in a specific term and year
    @Query("SELECT p FROM Performance p WHERE p.classroom.id = :classroomId AND p.term = :term AND p.year = :year")
    List<Performance> findByClassroomIdAndTermAndYear(
            @Param("classroomId") Long classroomId,
            @Param("term") Integer term,
            @Param("year") Integer year);

    // Get all performances for a student in a specific term and year
    @Query("SELECT p FROM Performance p WHERE p.student.id = :studentId AND p.term = :term AND p.year = :year")
    List<Performance> findByStudentIdAndTermAndYear(
            @Param("studentId") Long studentId,
            @Param("term") Integer term,
            @Param("year") Integer year);

    // Get all performances for a student
    @Query("SELECT p FROM Performance p WHERE p.student.id = :studentId")
    List<Performance> findByStudentId(@Param("studentId") Long studentId);

    // Get subject performance analysis for a class
    @Query("SELECT p.subject.name, AVG(p.marks), MAX(p.marks), MIN(p.marks), COUNT(p) " +
           "FROM Performance p " +
           "WHERE p.classroom.id = :classroomId AND p.term = :term AND p.year = :year " +
           "GROUP BY p.subject.id, p.subject.name")
    List<Object[]> getSubjectPerformanceAnalysis(
            @Param("classroomId") Long classroomId,
            @Param("term") Integer term,
            @Param("year") Integer year);

    // Get all distinct terms for a classroom
    @Query("SELECT DISTINCT p.term FROM Performance p WHERE p.classroom.id = :classroomId ORDER BY p.term DESC")
    List<Integer> findDistinctTermsByClassroomId(@Param("classroomId") Long classroomId);

    // Get all distinct years for a classroom
    @Query("SELECT DISTINCT p.year FROM Performance p WHERE p.classroom.id = :classroomId ORDER BY p.year DESC")
    List<Integer> findDistinctYearsByClassroomId(@Param("classroomId") Long classroomId);

    // Get all performances for a class, term, and year with student and subject
    @Query("SELECT p FROM Performance p " +
           "JOIN FETCH p.student " +
           "JOIN FETCH p.subject " +
           "WHERE p.classroom.id = :classroomId AND p.term = :term AND p.year = :year " +
           "ORDER BY p.student.name, p.subject.name")
    List<Performance> findByClassroomTermYear(
            @Param("classroomId") Long classroomId,
            @Param("term") Integer term,
            @Param("year") Integer year);
}

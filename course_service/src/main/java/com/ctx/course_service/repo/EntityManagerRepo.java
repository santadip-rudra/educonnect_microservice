package com.ctx.course_service.repo;

import com.ctx.course_service.dto.CourseCompletionStatsDTO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class EntityManagerRepo {

    @PersistenceContext
    private EntityManager entityManager;

    public List<CourseCompletionStatsDTO> getCourseCompletionStats() {

        String jpql = """
            SELECT c.title,
                   EXTRACT(YEAR FROM e.completedDate),
                   EXTRACT(MONTH FROM e.completedDate),
                   COUNT(e.enrollmentId),
                   (COUNT(e.enrollmentId) * 100.0 /
                       (SELECT COUNT(e2.enrollmentId)
                        FROM Enrollment e2
                        WHERE e2.course = e.course))
            FROM Enrollment e
            JOIN e.course c
            WHERE e.completedDate IS NOT NULL
            GROUP BY c.title, c.courseId,
                     EXTRACT(YEAR FROM e.completedDate),
                     EXTRACT(MONTH FROM e.completedDate)
            ORDER BY EXTRACT(YEAR FROM e.completedDate) ASC,
                     EXTRACT(MONTH FROM e.completedDate) ASC,
                     c.title ASC
            """;

        List<Object[]> results = entityManager.createQuery(jpql).getResultList();

        return results.stream()
                .filter(row -> row[0] != null  // courseTitle
                        && row[1] != null  // year
                        && row[2] != null  // month
                        && row[3] != null  // completedStudents
                        && row[4] != null) // completionPercentage
                .map(row -> new CourseCompletionStatsDTO(
                        (String)  row[0],
                        ((Number) row[1]).intValue(),
                        ((Number) row[2]).intValue(),
                        ((Number) row[3]).longValue(),
                        ((Number) row[4]).doubleValue()
                ))
                .toList();
    }
}

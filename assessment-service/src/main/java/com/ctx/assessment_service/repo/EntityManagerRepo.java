package com.ctx.assessment_service.repo;

import com.ctx.assessment_service.dto.result.CoursePassFailStatsDTO;
import com.ctx.assessment_service.dto.result.MonthlyAssessmentStatsDTO;
import com.ctx.assessment_service.dto.result.MonthlyExamStatsDTO;
import com.ctx.assessment_service.model.ResultStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Repository
public class EntityManagerRepo {
    @PersistenceContext
    private EntityManager entityManager;

    public List<MonthlyExamStatsDTO> getMonthlyExamStats() {

        String jpql =
        """
        SELECT
        EXTRACT(YEAR FROM r.createdAt),
        EXTRACT(MONTH FROM r.createdAt),
        AVG(r.percentageScore),
        COUNT(r.resultId),
        MAX(r.percentageScore)
        FROM Result r
        WHERE r.createdAt IS NOT NULL
        AND r.percentageScore IS NOT NULL
        GROUP BY EXTRACT(YEAR FROM r.createdAt), EXTRACT(MONTH FROM r.createdAt)
        ORDER BY EXTRACT(YEAR FROM r.createdAt) ASC, EXTRACT(MONTH FROM r.createdAt) ASC
        """;

        Query query = entityManager.createQuery(jpql);
        List<Object[]> results = query.getResultList();

        return results.stream()
                .map(row -> new MonthlyExamStatsDTO(
                        row[0] != null ? ((Number) row[0]).intValue()    : 0,
                        row[1] != null ? ((Number) row[1]).intValue()    : 0,
                        row[2] != null ? ((Number) row[2]).doubleValue() : 0.0,
                        row[3] != null ? ((Number) row[3]).longValue()   : 0L,
                        row[4] != null ? ((Number) row[4]).doubleValue() : 0.0
                ))
                .toList();
    }


    public List<MonthlyAssessmentStatsDTO> getMonthlyAssessmentAndSubmissionStats() {

        String assessmentJpql =
        """
        SELECT EXTRACT(YEAR FROM a.createdAt), EXTRACT(MONTH FROM a.createdAt), COUNT(a.assessmentId)
        FROM Assessment a
        WHERE a.createdAt IS NOT NULL
        GROUP BY EXTRACT(YEAR FROM a.createdAt), EXTRACT(MONTH FROM a.createdAt)
        ORDER BY EXTRACT(YEAR FROM a.createdAt) ASC, EXTRACT(MONTH FROM a.createdAt) ASC
        """;

        String submissionJpql =
        """
        SELECT EXTRACT(YEAR FROM s.createdAt), EXTRACT(MONTH FROM s.createdAt), COUNT(s.submissionId)
        FROM Submission s
        WHERE s.createdAt IS NOT NULL
        GROUP BY EXTRACT(YEAR FROM s.createdAt), EXTRACT(MONTH FROM s.createdAt)
        ORDER BY EXTRACT(YEAR FROM s.createdAt) ASC, EXTRACT(MONTH FROM s.createdAt) ASC
        """;

        List<Object[]> assessmentResults = entityManager.createQuery(assessmentJpql).getResultList();
        List<Object[]> submissionResults = entityManager.createQuery(submissionJpql).getResultList();

        // Build a map of year+month → counts from assessments
        Map<String, long[]> statsMap = new LinkedHashMap<>();

        for (Object[] row : assessmentResults) {
            if (row[0] == null || row[1] == null) continue;

            int year  = ((Number) row[0]).intValue();
            int month = ((Number) row[1]).intValue();
            long count = row[2] != null ? ((Number) row[2]).longValue() : 0L;

            String key = year + "-" + month;
            statsMap.computeIfAbsent(key, k -> new long[]{year, month, 0L, 0L});
            statsMap.get(key)[2] = count;
        }

        // Merge submission counts into the same map
        for (Object[] row : submissionResults) {
            if (row[0] == null || row[1] == null) continue;

            int year  = ((Number) row[0]).intValue();
            int month = ((Number) row[1]).intValue();
            long count = row[2] != null ? ((Number) row[2]).longValue() : 0L;

            String key = year + "-" + month;
            statsMap.computeIfAbsent(key, k -> new long[]{year, month, 0L, 0L});
            statsMap.get(key)[3] = count;
        }

        // Convert map to DTO list
        return statsMap.values().stream()
                .map(data -> new MonthlyAssessmentStatsDTO(
                        (int) data[0],  // year
                        (int) data[1],  // month
                        data[2],        // totalAssessments
                        data[3]         // totalSubmissions
                ))
                .toList();
    }

    public List<CoursePassFailStatsDTO> getCoursePassFailStats() {

        String jpql = """
        SELECT
            r.assessment.courseId,
            COUNT(r.resultId),
            SUM(CASE WHEN r.status = :passed THEN 1 ELSE 0 END),
            SUM(CASE WHEN r.status = :failed THEN 1 ELSE 0 END)
        FROM Result r
        JOIN r.assessment a
        WHERE r.status IS NOT NULL
        GROUP BY a.courseId
        """;

        List<Object[]> rows = entityManager.createQuery(jpql)
                .setParameter("passed", ResultStatus.PASSED)
                .setParameter("failed", ResultStatus.FAILED)
                .getResultList();



        return rows.stream()
                .map(row -> new CoursePassFailStatsDTO(
                        UUID.fromString(row[0].toString()),
                        ((Number) row[1]).longValue(),
                        ((Number) row[2]).longValue(),
                        ((Number) row[3]).longValue()
                ))
                .toList();
    }
}

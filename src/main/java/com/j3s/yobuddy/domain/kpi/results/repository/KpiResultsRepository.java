package com.j3s.yobuddy.domain.kpi.results.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.j3s.yobuddy.domain.kpi.results.entity.KpiResults;

@Repository
public interface KpiResultsRepository extends JpaRepository<KpiResults, Long> {

    List<KpiResults> findAllByIsDeletedFalse();

    Optional<KpiResults> findByKpiResultIdAndIsDeletedFalse(Long kpiResultId);

    List<KpiResults> findByKpiGoalIdAndIsDeletedFalse(Long kpiGoalId);

    List<KpiResults> findByUserIdAndIsDeletedFalse(Long userId);

    List<KpiResults> findByDepartmentIdAndIsDeletedFalse(Long departmentId);

    Optional<KpiResults> findTopByUserIdAndKpiGoalIdAndIsDeletedFalseOrderByEvaluatedAtDesc(
        Long userId,
        Long kpiGoalId
    );

    @Query("""
select
    r.kpiGoalId as kpiGoalId,
    avg(r.achievedValue) as avgScore
from KpiResults r
where r.departmentId = :departmentId
  and r.isDeleted = false
group by r.kpiGoalId
""")
    List<DeptKpiAvgProjection> findDeptAvgKpi(
        Long departmentId
    );

    @Query("""
    select avg(r.achievedValue)
    from KpiResults r
    where r.userId = :userId
      and r.evaluatedAt between :start and :end
      and r.isDeleted = false
""")
    Double findUserAvgKpi(
        Long userId,
        LocalDateTime start,
        LocalDateTime end
    );

    @Query("""
    select avg(r.achievedValue)
    from KpiResults r
    where r.departmentId = :departmentId
      and r.evaluatedAt between :start and :end
      and r.isDeleted = false
""")
    Double findDeptAvgKpi(
        Long departmentId,
        LocalDateTime start,
        LocalDateTime end
    );
}

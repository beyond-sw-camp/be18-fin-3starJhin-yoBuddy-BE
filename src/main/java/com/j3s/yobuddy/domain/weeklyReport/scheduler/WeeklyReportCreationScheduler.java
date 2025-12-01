package com.j3s.yobuddy.domain.weeklyReport.scheduler;


import com.j3s.yobuddy.domain.mentor.menteeAssignment.entity.MentorMenteeAssignment;
import com.j3s.yobuddy.domain.mentor.menteeAssignment.repository.MentorMenteeAssignmentRepository;
import com.j3s.yobuddy.domain.programenrollment.entity.ProgramEnrollment;
import com.j3s.yobuddy.domain.programenrollment.entity.ProgramEnrollment.EnrollmentStatus;
import com.j3s.yobuddy.domain.programenrollment.repository.ProgramEnrollmentRepository;
import com.j3s.yobuddy.domain.weeklyReport.entity.WeeklyReport;
import com.j3s.yobuddy.domain.weeklyReport.entity.WeeklyReport.WeeklyReportStatus;
import com.j3s.yobuddy.domain.weeklyReport.repository.WeeklyReportRepository;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class WeeklyReportCreationScheduler {

    private final ProgramEnrollmentRepository programEnrollmentRepository;
    private final WeeklyReportRepository weeklyReportRepository;
    private final MentorMenteeAssignmentRepository mentorMenteeAssignmentRepository;

    /**
     * 매주 월요일 오전 10시에 실행 (테스트용)
     * 운영은 "0 0 1 * * MON"
     */
    @Scheduled(cron = "0 10 10 * * MON")
    @Transactional
    public void createWeeklyReports() {

        LocalDate today = LocalDate.now();
        log.info("[WeeklyReportCreationScheduler] Start - today={}", today);

        // 월요일 아닌 날에도 혹시라도 실행됐을 때 대비 (방어 로직)
        if (!today.getDayOfWeek().name().equals("MONDAY")) {
            log.info("[WeeklyReportCreationScheduler] Skip - today is not Monday");
            return;
        }

        // 1. ACTIVE 상태의 프로그램 참여 전체 조회
        List<ProgramEnrollment> activeEnrollments =
            programEnrollmentRepository.findByStatus(EnrollmentStatus.ACTIVE);

        int createdCount = 0;

        for (ProgramEnrollment enrollment : activeEnrollments) {
            var program = enrollment.getProgram();
            LocalDate programStartDate = program.getStartDate();
            LocalDate programEndDate = program.getEndDate();

            // 프로그램 시작일 없거나 아직 시작 전이면 skip
            if (programStartDate == null || today.isBefore(programStartDate)) {
                continue;
            }

            // 프로그램 종료 후면 skip
            if (programEndDate != null && today.isAfter(programEndDate)) {
                continue;
            }

            // 2. (프로그램시작일 → 오늘) 몇 번째 주인지 계산
            long weeksBetween = ChronoUnit.WEEKS.between(programStartDate, today);
            int weekNumber = (int) weeksBetween + 1;

            // 이번 주 리포트 기간 (월 ~ 금)
            LocalDate weekStartDate = programStartDate.plusWeeks(weeksBetween);
            LocalDate weekEndDate = weekStartDate.plusDays(4);

            Long menteeId = enrollment.getUser().getUserId();

            // 3. 이미 생성되어 있으면 skip
            if (weeklyReportRepository.existsByMenteeIdAndWeekNumber(menteeId, weekNumber)) {
                continue;
            }

            // 4. 멘토 매핑 확인
            Optional<MentorMenteeAssignment> mentorOpt =
                mentorMenteeAssignmentRepository.findByMenteeUserIdAndDeletedFalse(menteeId);

            if (mentorOpt.isEmpty()) {
                log.warn("[WeeklyReportCreationScheduler] Mentor not found - menteeId={}", menteeId);
                continue;
            }

            Long mentorId = mentorOpt.get().getMentor().getUserId();

            // 5. WeeklyReport 생성
            WeeklyReport report = WeeklyReport.builder()
                .weekNumber(weekNumber)
                .startDate(weekStartDate)
                .endDate(weekEndDate)
                .status(WeeklyReportStatus.DRAFT)
                .mentorId(mentorId)
                .menteeId(menteeId)
                .build();

            weeklyReportRepository.save(report);
            createdCount++;

            log.info(
                "[WeeklyReportCreationScheduler] Created WeeklyReport - menteeId={}, weekNumber={}, start={}, end={}",
                menteeId,
                weekNumber,
                weekStartDate,
                weekEndDate
            );
        }

        log.info("[WeeklyReportCreationScheduler] Completed - createdCount={}", createdCount);
    }
}

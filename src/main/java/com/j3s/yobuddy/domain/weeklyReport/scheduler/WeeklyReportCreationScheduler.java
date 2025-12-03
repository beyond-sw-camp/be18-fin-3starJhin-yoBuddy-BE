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

    @Scheduled(cron = "0 0 1 * * MON")
    @Transactional
    public void createWeeklyReports() {

        LocalDate today = LocalDate.now();
        log.info("[WeeklyReportCreationScheduler] Start - today={}", today);

        if (!today.getDayOfWeek().name().equals("MONDAY")) {
            log.info("[WeeklyReportCreationScheduler] Skip - today is not Monday");
            return;
        }

        List<ProgramEnrollment> activeEnrollments =
            programEnrollmentRepository.findByStatus(EnrollmentStatus.ACTIVE);

        int createdCount = 0;

        for (ProgramEnrollment enrollment : activeEnrollments) {
            var program = enrollment.getProgram();
            LocalDate programStartDate = program.getStartDate();
            LocalDate programEndDate = program.getEndDate();

            if (programStartDate == null || today.isBefore(programStartDate)) {
                continue;
            }

            if (programEndDate != null && today.isAfter(programEndDate)) {
                continue;
            }

            long weeksBetween = ChronoUnit.WEEKS.between(programStartDate, today);
            int weekNumber = (int) weeksBetween + 1;

            LocalDate weekStartDate = programStartDate.plusWeeks(weeksBetween);
            LocalDate weekEndDate = weekStartDate.plusDays(4);

            Long menteeId = enrollment.getUser().getUserId();

            if (weeklyReportRepository.existsByMenteeIdAndWeekNumber(menteeId, weekNumber)) {
                continue;
            }

            Optional<MentorMenteeAssignment> mentorOpt =
                mentorMenteeAssignmentRepository.findByMenteeUserIdAndDeletedFalse(menteeId);

            if (mentorOpt.isEmpty()) {
                log.warn("[WeeklyReportCreationScheduler] Mentor not found - menteeId={}", menteeId);
                continue;
            }

            Long mentorId = mentorOpt.get().getMentor().getUserId();

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

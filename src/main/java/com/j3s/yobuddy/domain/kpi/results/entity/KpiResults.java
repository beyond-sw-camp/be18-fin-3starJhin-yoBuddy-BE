package com.j3s.yobuddy.domain.kpi.results.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "kpi_results")
public class KpiResults {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "kpi_result_id", nullable = false)
	private Long kpiResultId;

	@Column(name = "achieved_value", precision = 10, scale = 2)
	private BigDecimal achievedValue;

	@Column(precision = 10, scale = 2)
	private BigDecimal score;

	@Column(name = "evaluated_at")
	private LocalDateTime evaluatedAt;

	@Column(name = "kpi_goal_id")
	private Long kpiGoalId;

	@Column(name = "user_id")
	private Long userId;

	@Column(name = "department_id")
	private Long departmentId;

	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at")
	private LocalDateTime updatedAt;

	@Column(name = "is_deleted", nullable = false)
	@Builder.Default
	private Boolean isDeleted = false;

	@PrePersist
	protected void onCreate() {
		this.createdAt = LocalDateTime.now();
		this.updatedAt = LocalDateTime.now();
	}

	public void update(BigDecimal achievedValue, BigDecimal score, LocalDateTime evaluatedAt,
		Long kpiGoalId, Long userId, Long departmentId) {
		boolean updated = false;
		if (achievedValue != null) {
			this.achievedValue = achievedValue;
			updated = true;
		}
		if (score != null) {
			this.score = score;
			updated = true;
		}
		if (evaluatedAt != null) {
			this.evaluatedAt = evaluatedAt;
			updated = true;
		}
		if (kpiGoalId != null) {
			this.kpiGoalId = kpiGoalId;
			updated = true;
		}
		if (userId != null) {
			this.userId = userId;
			updated = true;
		}
		if (departmentId != null) {
			this.departmentId = departmentId;
			updated = true;
		}
		if (updated) {
			this.updatedAt = LocalDateTime.now();
		}
	}

	public void softDelete() {
		this.isDeleted = true;
		this.updatedAt = LocalDateTime.now();
	}
}


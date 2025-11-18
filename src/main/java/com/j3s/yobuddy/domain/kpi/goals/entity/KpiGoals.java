package com.j3s.yobuddy.domain.kpi.goals.entity;

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
@Table(name = "kpi_goals")
public class KpiGoals {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "kpi_goal_id", nullable = false)
	private Long kpiGoalId;

	@Column(name = "program_id")
	private Long programId;

	@Column(columnDefinition = "TEXT")
	private String description;

	@Column(name = "target_value")
	private Integer targetValue;

	private BigDecimal weight;

	@Column(name = "kpi_category_id")
	private Long kpiCategoryId;

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

	public void update(Long programId, String description, Integer targetValue, BigDecimal weight,
		Long kpiCategoryId) {
		boolean updated = false;
		if (programId != null) {
			this.programId = programId;
			updated = true;
		}
		if (description != null) {
			this.description = description;
			updated = true;
		}
		if (targetValue != null) {
			this.targetValue = targetValue;
			updated = true;
		}
		if (weight != null) {
			this.weight = weight;
			updated = true;
		}
		if (kpiCategoryId != null) {
			this.kpiCategoryId = kpiCategoryId;
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


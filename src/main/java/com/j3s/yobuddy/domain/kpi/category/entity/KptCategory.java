package com.j3s.yobuddy.domain.kpi.category.entity;

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
@Table(name = "kpi_category")
public class KptCategory {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "kpi_category_id", nullable = false)
	private Long kpiCategoryId;

	@Column(nullable = false, length = 250)
	private String name;

	@Column(columnDefinition = "TEXT")
	private String description;

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

	public void update(String name, String description) {
		boolean updated = false;
		if (name != null && !name.isBlank()) {
			this.name = name;
			updated = true;
		}
		if (description != null) {
			this.description = description;
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

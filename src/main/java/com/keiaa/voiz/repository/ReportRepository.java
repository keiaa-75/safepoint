package com.keiaa.voiz.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.keiaa.voiz.model.Report;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
}
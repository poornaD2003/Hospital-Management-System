package com.example.pharmacy.repository;

import com.example.pharmacy.model.PrescriptionIssue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PrescriptionIssueRepository extends JpaRepository<PrescriptionIssue, Long> {}
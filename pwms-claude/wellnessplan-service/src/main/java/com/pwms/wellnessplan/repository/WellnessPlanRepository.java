package com.pwms.wellnessplan.repository;

import com.pwms.wellnessplan.model.WellnessPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WellnessPlanRepository extends JpaRepository<WellnessPlan, Integer> {

    Optional<WellnessPlan> findByPlanName(String planName);
    boolean existsByPlanName(String planName);
}

package com.pwms.wellnessplan.repository;

import com.pwms.wellnessplan.model.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, Integer> {

    // Used by ProgressService to fetch activities for a plan
    List<Activity> findByPlan_PlanId(int planId);
}

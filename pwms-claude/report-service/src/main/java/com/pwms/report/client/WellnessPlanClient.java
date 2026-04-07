package com.pwms.report.client;

import com.pwms.report.dto.PlanDTO;
import com.pwms.report.dto.PlanAssignmentDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class WellnessPlanClient {

    private final RestTemplate restTemplate;

    @Value("${services.wellness.url}")
    private String wellnessUrl;

    // GET /api/plans/{planId}
    public PlanDTO getPlanById(int planId) {
        String url = wellnessUrl + "/api/plans/" + planId;
        try {
            return restTemplate.getForObject(url, PlanDTO.class);
        } catch (Exception e) {
            log.error("Failed to fetch plan {}: {}", planId, e.getMessage());
            throw new RuntimeException("Plan not found: " + planId);
        }
    }

    // GET /api/plans/assignments/patient/{patientId}
    public List<PlanAssignmentDTO> getActiveAssignmentsByPatient(int patientId) {
        String url = wellnessUrl + "/api/plans/assignments/patient/" + patientId;
        try {
            ResponseEntity<List<PlanAssignmentDTO>> response = restTemplate.exchange(
                    url, HttpMethod.GET, null,
                    new ParameterizedTypeReference<List<PlanAssignmentDTO>>() {}
            );
            return response.getBody();
        } catch (Exception e) {
            log.error("Failed to fetch assignments for patient {}: {}", patientId, e.getMessage());
            return List.of();
        }
    }
}
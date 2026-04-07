package com.pwms.report.client;

import com.pwms.report.dto.ProgressSummaryDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProgressClient {

    private final RestTemplate restTemplate;

    @Value("${services.progress.url}")
    private String progressUrl;

    // GET /api/progress/summary/{patientId}/plan/{planId}
    public ProgressSummaryDTO getDailySummary(int patientId, int planId) {
        String url = progressUrl +
                "/api/progress/summary/" + patientId + "/plan/" + planId;
        try {
            return restTemplate.getForObject(url, ProgressSummaryDTO.class);
        } catch (Exception e) {
            log.error("Failed to fetch progress summary: {}", e.getMessage());
            throw new RuntimeException("Progress summary not found for patient: " + patientId);
        }
    }
}
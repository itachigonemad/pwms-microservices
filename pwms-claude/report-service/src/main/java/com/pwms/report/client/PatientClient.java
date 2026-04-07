package com.pwms.report.client;

import com.pwms.report.dto.PatientDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
@Slf4j
public class PatientClient {

    private final RestTemplate restTemplate;

    @Value("${services.patient.url}")
    private String patientUrl;

    // GET /api/patients/{patientId}
    public PatientDTO getPatientById(int patientId) {
        String url = patientUrl + "/api/patients/" + patientId;
        try {
            return restTemplate.getForObject(url, PatientDTO.class);
        } catch (Exception e) {
            log.error("Failed to fetch patient {}: {}", patientId, e.getMessage());
            throw new RuntimeException("Patient not found: " + patientId);
        }
    }
}

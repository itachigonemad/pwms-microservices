package com.pwms.progress.client;

import com.pwms.progress.dto.ActivityDTO;
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

    // GET /api/plans/{planId}/activities
    public List<ActivityDTO> getActivitiesByPlanId(int planId) {
        String url = wellnessUrl + "/api/plans/" + planId + "/activities";
        log.info("Fetching activities for planId={} from wellnessplan-service", planId);
        try {
            ResponseEntity<List<ActivityDTO>> response = restTemplate.exchange(
                    url, HttpMethod.GET, null,
                    new ParameterizedTypeReference<List<ActivityDTO>>() {}
            );
            return response.getBody();
        } catch (Exception e) {
            log.error("Failed to fetch activities: {}", e.getMessage());
            throw new RuntimeException("Failed to fetch activities for planId: " + planId);
        }
    }

    // GET /api/plans/{planId}
    public boolean planExists(int planId) {
        String url = wellnessUrl + "/api/plans/" + planId;
        try {
            restTemplate.getForObject(url, Object.class);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}


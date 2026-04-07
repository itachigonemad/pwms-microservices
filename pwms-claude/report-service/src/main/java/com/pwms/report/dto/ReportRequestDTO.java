package com.pwms.report.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportRequestDTO {
    private int    adminId;
    private String adminSummary;    // written by admin
}

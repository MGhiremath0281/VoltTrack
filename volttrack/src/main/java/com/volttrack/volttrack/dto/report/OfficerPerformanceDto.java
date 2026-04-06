package com.volttrack.volttrack.dto.report;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OfficerPerformanceDto {
    private List<String> officerNames;     // e.g., ["Officer A", "Officer B"]
    private List<Integer> metersHandled;   // number of meters managed
    private List<Integer> approvalsDone;   // number of approvals
    private List<Integer> complaintsResolved; // number of complaints resolved
}

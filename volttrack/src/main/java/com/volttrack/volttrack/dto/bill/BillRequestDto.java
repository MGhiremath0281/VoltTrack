package com.volttrack.volttrack.dto.bill;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import com.volttrack.volttrack.entity.Status;
import com.volttrack.volttrack.entity.Billing;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BillRequestDto {
    private Long meterId;   
}

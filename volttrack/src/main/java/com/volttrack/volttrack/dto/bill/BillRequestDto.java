package com.volttrack.volttrack.dto.bill;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BillRequestDto {

    private String meterPublicId;
}
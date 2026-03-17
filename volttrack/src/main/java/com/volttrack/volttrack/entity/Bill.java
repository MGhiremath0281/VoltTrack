package com.volttrack.volttrack.entity;

import com.volttrack.volttrack.entity.enums.BillStatus;
import com.volttrack.volttrack.entity.enums.BillingCycle;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "bills")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Bill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ✅ External identifier with prefix (e.g., BILL-1)
    @Column(name = "public_id", unique = true, nullable = false)
    private String publicId;

    @ManyToOne
    @JoinColumn(name = "meter_id", nullable = false)
    private Meter meter;

    @ManyToOne
    @JoinColumn(name = "consumer_id", nullable = false)
    private User consumer;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BillingCycle billingCycle; // MONTHLY / FIFTEEN_DAYS

    private LocalDateTime cycleStartDate;
    private LocalDateTime cycleEndDate;

    private Double openingReading;
    private Double closingReading;
    private Double unitsConsumed;

    private Double baseAmount;
    private Double fixedCharges;
    private Double taxAmount;
    private Double totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BillStatus status;

    private LocalDateTime generatedAt;
    private LocalDateTime dueDate;
}

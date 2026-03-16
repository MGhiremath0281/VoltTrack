package com.volttrack.volttrack.security;

import com.volttrack.volttrack.dto.meter.MeterRequestDto;
import com.volttrack.volttrack.entity.Meter;
import com.volttrack.volttrack.repository.MeterRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

@Component("meterSecurity")
public class MeterSecurity {

    private final MeterRepository meterRepository;

    public MeterSecurity(MeterRepository meterRepository) {
        this.meterRepository = meterRepository;
    }

    public boolean isOwner(Long meterId, Authentication authentication) {

        CustomUserDetails user = (CustomUserDetails) authentication.getPrincipal();

        return meterRepository.findById(meterId)
                .map(meter -> meter.getUser().getId().equals(user.getId()))
                .orElse(false);
    }

    public boolean canCreateMeter(MeterRequestDto dto, Authentication authentication) {

        CustomUserDetails user = (CustomUserDetails) authentication.getPrincipal();

        boolean isAdminOrOfficer = user.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role ->
                        role.equals("ROLE_ADMIN") ||
                                role.equals("ROLE_OFFICER")
                );

        if (isAdminOrOfficer) {
            return true;
        }

        // CONSUMER
        return dto.getUserId().equals(user.getId());
    }
}
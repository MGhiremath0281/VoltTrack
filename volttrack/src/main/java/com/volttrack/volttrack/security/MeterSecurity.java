package com.volttrack.volttrack.security;

import com.volttrack.volttrack.dto.meter.MeterRequestDto;
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

    public boolean isOwner(String publicId, Authentication authentication) {

        CustomUserDetails user = (CustomUserDetails) authentication.getPrincipal();

        return meterRepository.findByPublicId(publicId)
                .map(meter -> meter.getUser().getPublicId().equals(user.getPublicId()))
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

        return dto.getUserPublicId().equals(user.getPublicId());
    }
}
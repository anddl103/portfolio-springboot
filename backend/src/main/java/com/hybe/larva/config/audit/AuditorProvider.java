package com.hybe.larva.config.audit;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

@Slf4j
public class AuditorProvider implements AuditorAware<String> {

    @Override
    public @NotNull Optional<String> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.of("unknown");
        }
        Object details = authentication.getDetails();
        if (details instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) details;
            log.debug("current auditor: {}", userDetails.getUsername());
            return Optional.of(userDetails.getUsername());
        } else {
            return Optional.of(authentication.getPrincipal().toString());
        }
    }
}

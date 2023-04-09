package com.hybe.larva.entity.user_info;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;

@ToString
@Data
@Builder
public class AccountAgree {

    private boolean flag;

    private LocalDateTime updatedAt;
}

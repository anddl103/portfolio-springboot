package com.hybe.larva.dto.notice;

import com.hybe.larva.entity.common.LocaleCodeContents;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.Map;

@ToString
@Getter
@Builder
public class NoticeUpdateRequest {

    private Map<String, LocaleCodeContents> localeCodeContents;

    private LocalDateTime noticeAt;

}

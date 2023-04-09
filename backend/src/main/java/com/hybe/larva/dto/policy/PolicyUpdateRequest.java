package com.hybe.larva.dto.policy;

import com.hybe.larva.entity.common.LocaleCodeContents;
import com.hybe.larva.enums.Usage;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import java.util.Map;

@ToString
@Getter
@Builder
public class PolicyUpdateRequest {

    @ApiModelProperty(dataType = "java.lang.String")
    private Usage usage;

    private Map<String, LocaleCodeContents> localeCodeContents;
}

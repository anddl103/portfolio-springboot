package com.hybe.larva.dto.faq;

import com.hybe.larva.entity.common.LocaleCodeContents;
import com.hybe.larva.enums.Category;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.data.mongodb.core.index.Indexed;

import javax.validation.constraints.NotBlank;
import java.util.Map;

@ToString
@Getter
@Builder
public class FaqUpdateRequest {

    @NotBlank
    private String category;

    private Map<String, LocaleCodeContents> localeCodeContents;
}

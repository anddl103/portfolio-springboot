package com.hybe.larva.entity.common;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@ToString
@Data
@Builder
public class LocaleCodeContents {

    private String subject;

    private String contents;
}

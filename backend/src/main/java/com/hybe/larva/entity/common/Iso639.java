package com.hybe.larva.entity.common;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Getter
@NoArgsConstructor
public class Iso639 {

    // refer to: https://ko.wikipedia.org/wiki/ISO_639-1_코드_목록

    @ApiModelProperty(value = "한국어")
    private String ko;  // korean

    @ApiModelProperty(value = "영어")
    private String en;  // english

    @ApiModelProperty(value = "일본어")
    private String ja;  // japanese

    /* not supported yet
    @ApiModelProperty(value = "중국어")
    private String zh;  // chinese*/

    /* not supported yet
    @ApiModelProperty(value = "스페인어")
    private String es;  // Spanish*/

}

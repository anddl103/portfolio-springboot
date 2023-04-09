package com.hybe.larva.entity.common;

import io.swagger.annotations.ApiModelProperty;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@NoArgsConstructor
public class Iso3166 {

    // refer to: https://ko.wikipedia.org/wiki/ISO_3166-1

    @ApiModelProperty(value = "한국")
    public String KR;  // korean

    @ApiModelProperty(value = "미국")
    public String US;  // english

    @ApiModelProperty(value = "일본")
    public String JP;  // japanese

}

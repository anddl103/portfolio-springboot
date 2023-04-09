package com.hybe.larva.controller.backoffice_api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
public class RedirectController {

    @RequestMapping(value = "/{path:(?!swagger-ui)[^\\.]*}")
    public String redirect() {
        log.info("=================== 1");
        return "forward:/";

    }

    @RequestMapping(value = "/{path:[^\\.]*}/{path:[^\\.]*}")
    public String redirect2() {
        log.info("=================== 2");
        return "forward:/";
    }
}

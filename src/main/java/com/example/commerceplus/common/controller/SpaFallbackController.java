package com.example.commerceplus.common.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class SpaFallbackController {
    @RequestMapping(
            value = "/{path:^(?!api|portone|auth|assets|favicon\\.ico|index\\.html).*$}/**",
            method = RequestMethod.GET
    )
    public String forward() {
        return "forward:/index.html";
    }
}

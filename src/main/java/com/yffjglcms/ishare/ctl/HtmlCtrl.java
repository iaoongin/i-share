package com.yffjglcms.ishare.ctl;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * HtmlCtrl
 *
 * @author xiaohongxin
 * @version 1.0.0
 * @date 2020/6/3 22:30
 */
@Controller
public class HtmlCtrl {

    @GetMapping("/")
    public String home() {
        return "index";
    }

}

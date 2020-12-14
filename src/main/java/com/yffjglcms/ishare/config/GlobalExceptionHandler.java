package com.yffjglcms.ishare.config;

import cn.hutool.json.JSONUtil;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * GlobalExceptionHandler
 *
 * @author xiaohongxin
 * @version 1.0.0
 * @date 2020/12/3 10:51
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    @ResponseBody
    @ExceptionHandler(Exception.class)
    public String exception(Exception ex){

        return JSONUtil.toJsonStr(ex.toString());
    }

}

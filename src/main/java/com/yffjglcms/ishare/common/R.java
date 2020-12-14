package com.yffjglcms.ishare.common;

import lombok.Data;

/**
 * R
 *
 * @author xiaohongxin
 * @version 1.0.0
 * @date 2020/6/4 10:59
 */
@Data
public class R<T> {

    private int code;
    private T data;
    private String[] msg;

    public static R<Void> fail() {
        return fail("异常.");
    }

    public static R<Void> fail(String... msg) {
        R<Void> r = new R<>();
        r.code = 0;
        r.data = null;
        r.msg = msg;
        return r;
    }

    public static <T> R<T> ok(T data) {
        R<T> r = new R<>();
        r.code = 200;
        r.data = data;
        return r;
    }
}

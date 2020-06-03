package com.yffjglcms.ishare.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * Message
 *
 * @author xiaohongxin
 * @version 1.0.0
 * @date 2020/6/3 22:10
 */
@Data
public class Message<T> implements Serializable {

    private String uuid;
    private String ip;
    private String code;
    private LocalDate createAt;
    private T content;

}

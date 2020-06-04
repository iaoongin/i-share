package com.yffjglcms.ishare.entity;

import com.yffjglcms.ishare.consts.MessageType;
import lombok.Data;

/**
 * MessageResp
 *
 * @author xiaohongxin
 * @version 1.0.0
 * @date 2020/6/4 10:58
 */
@Data
public class MessageResp<T> {

    private T content;
    private MessageType type;

}

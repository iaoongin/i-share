package com.yffjglcms.ishare.ctl;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.yffjglcms.ishare.common.R;
import com.yffjglcms.ishare.consts.MessageType;
import com.yffjglcms.ishare.entity.Base64ImgMessage;
import com.yffjglcms.ishare.entity.Message;
import com.yffjglcms.ishare.entity.MessageResp;
import com.yffjglcms.ishare.entity.SimpleTxtMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.LocalDate;

/**
 * MessageCtrl
 *
 * @author xiaohongxin
 * @version 1.0.0
 * @date 2020/6/3 22:14
 */
@Slf4j
@RequestMapping("/message")
@RestController
public class MessageCtrl {

    Cache<String, Message> cache = CacheBuilder.newBuilder()
            .initialCapacity(100)
            .expireAfterAccess(Duration.ofHours(2))
            .expireAfterWrite(Duration.ofHours(2))
            .build();

    @PostMapping
    public R save(@RequestBody Message message, MessageType type) {
        String uuid = message.getUuid();

        synchronized (cache) {
            try {
                Message ifPresent = cache.getIfPresent(uuid);

                // 已存在
                if (ifPresent != null) {
                    return R.fail("已存在.");
                }

                if (type == null) {
                    type = MessageType.SimpleTxtMessage;
                }
                switch (type) {
                    case Base64ImgMessage:
                        Base64ImgMessage base64ImgMessage = new Base64ImgMessage();

                        BeanUtils.copyProperties(message, base64ImgMessage);

                        base64ImgMessage.setCreateAt(LocalDate.now());
                        cache.put(uuid, base64ImgMessage);
                        break;
                    case SimpleTxtMessage:
                    default:
                        SimpleTxtMessage simpleTxtMessage = new SimpleTxtMessage();

                        BeanUtils.copyProperties(message, simpleTxtMessage);

                        simpleTxtMessage.setCreateAt(LocalDate.now());
                        cache.put(uuid, simpleTxtMessage);
                        break;

                }


            } catch (Exception e) {
                if (log.isDebugEnabled()) {
                    log.error(e.getMessage(), e);
                } else {
                    log.error(e.getMessage());
                }
            }
        }

        return R.ok(null);
    }

    @GetMapping("{uuid}")
    public R get(@PathVariable("uuid") String uuid, String code) {

        Message ifPresent = cache.getIfPresent(uuid);
        if (ifPresent == null) {
            return R.fail();
        }

        String trueCode = ifPresent.getCode();
        if (!(StringUtils.isEmpty(trueCode) || trueCode.equals(code))) {
            return R.fail("校验失败");
        }
        MessageResp messageResp = new MessageResp<>();
        messageResp.setContent(ifPresent.getContent());

        if (ifPresent instanceof Base64ImgMessage) {
            messageResp.setType(MessageType.Base64ImgMessage);
        } else {
            messageResp.setType(MessageType.SimpleTxtMessage);
        }

        return R.ok(messageResp);
    }

}

package com.yffjglcms.ishare.ctl;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.yffjglcms.ishare.consts.MessageType;
import com.yffjglcms.ishare.entity.Base64ImgMessage;
import com.yffjglcms.ishare.entity.Message;
import com.yffjglcms.ishare.entity.SimpleTxtMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
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
@Controller
public class MessageCtrl {

    Cache<String, Message> cache = CacheBuilder.newBuilder()
            .initialCapacity(100)
            .expireAfterAccess(Duration.ofHours(2))
            .expireAfterWrite(Duration.ofHours(2))
            .build();

    @PostMapping
    @ResponseBody
    public int save(@RequestBody Message message, MessageType type) {
        String uuid = message.getUuid();

        synchronized (cache) {
            try {
                Message ifPresent = cache.getIfPresent(uuid);

                // 已存在
                if (ifPresent != null) {
                    return 0;
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

        return 1;
    }

    @GetMapping("{uuid}")
    public void get(@PathVariable("uuid") String uuid, String code) throws IOException {

        Message ifPresent = cache.getIfPresent(uuid);
        if (ifPresent == null) {
            return;
        }

        String trueCode = ifPresent.getCode();
        if (!(StringUtils.isEmpty(trueCode) || trueCode.equals(code))) {
            writerStr("校验失败。");
            return;
        }

        if (ifPresent instanceof Base64ImgMessage) {
            String content = (String) ifPresent.getContent();
            writerImg(content);
            return;
        }

        writerJson(ifPresent.getContent());

    }

    private void writerStr(String s) throws IOException {
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletResponse response = servletRequestAttributes.getResponse();
        response.setContentType(MediaType.TEXT_HTML_VALUE);
        PrintWriter writer = response.getWriter();
        writer.println(s);
        writer.flush();
    }

    private void writerJson(Object obj) throws IOException {
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletResponse response = servletRequestAttributes.getResponse();
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        PrintWriter writer = response.getWriter();
        writer.println(JSONUtil.toJsonStr(obj));
        writer.flush();
    }

    private void writerImg(String base64Str) throws IOException {
        boolean notEmpty = StrUtil.isNotEmpty(base64Str);
        if (!notEmpty) {
            return;
        }

        base64Str = base64Str.replaceAll("data:image/(png|jpeg|gif);base64,", "");

        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletResponse response = servletRequestAttributes.getResponse();
        response.setContentType(MediaType.IMAGE_PNG_VALUE);
        ServletOutputStream outputStream = response.getOutputStream();
        Base64.decodeToStream(base64Str, outputStream, false);
        outputStream.flush();
        outputStream.close();
    }

}

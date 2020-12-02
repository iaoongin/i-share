package com.yffjglcms.ishare.ctl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.yffjglcms.ishare.entity.oauth2.StateData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientProperties;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * TODO
 *
 * @author xiaohongxin
 * @version 1.0.0
 * @date 2020/10/23 15:46
 */
@Controller
@RequestMapping("/oauth2")
@Slf4j
public class Oauth2Controller {

//    @Resource
//    private OAuth2RestTemplate oAuth2RestTemplate;

    private static final Map<String, Object> stateToDataMap = new ConcurrentHashMap<>();

    @Resource
    private OAuth2ClientProperties oAuth2ClientProperties;

    //    @ResponseBody
    @GetMapping("/code/{registrationId}")
    public JSONObject code(@PathVariable("registrationId") String registrationId, String code, String state) throws Exception {

        StateData stateData = (StateData) stateToDataMap.get(state);

        OAuth2ClientProperties.Provider provider = oAuth2ClientProperties.getProvider().get(registrationId);
        OAuth2ClientProperties.Registration registration = oAuth2ClientProperties.getRegistration().get(registrationId);

        if (provider == null) {
            throw new Exception("不支持的provider registrationId:" + registrationId);
        }
        if (registration == null) {
            throw new Exception("不支持的registration registrationId:" + registrationId);
        }


        String tokenUri = provider.getTokenUri();
        HttpRequest post = HttpUtil.createPost(tokenUri);
        post.header(Header.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE);
//        post.header(Header.AUTHORIZATION,);
        post.basicAuth(registration.getClientId(), registration.getClientSecret());

        Map<String, Object> param = new HashMap<>();
        param.put("grant_type", registration.getAuthorizationGrantType());
        param.put("code", code);
        param.put("client_id", registration.getClientId());
        param.put("scope", CollUtil.join(registration.getScope(), " "));
//        param.set("redirect_uri", code);

        String paramStr = param.toString();

        post.body(HttpUtil.toParams(param));

        System.out.println(post.toString());
        String body = post.execute().body();

        JSONObject jsonObject = new JSONObject();
        jsonObject.set("registrationId", registrationId);
        jsonObject.set("code", code);
        jsonObject.set("stateData", stateData);
        JSONObject respBody = JSONUtil.parseObj(body);
        jsonObject.set("body", respBody);

        String redirectUrl = stateData.getRedirectUrl() + "?data=" + URLEncoder.encode(body, CharsetUtil.UTF_8);
        response.sendRedirect(redirectUrl);
        return jsonObject;
    }

    @Resource
    private HttpServletResponse response;


    @GetMapping("/custom/authorization/{registrationId}")
    public void authorization(@PathVariable("registrationId") String registrationId, StateData stateData) throws Exception {

        OAuth2ClientProperties.Provider provider = oAuth2ClientProperties.getProvider().get(registrationId);
        OAuth2ClientProperties.Registration registration = oAuth2ClientProperties.getRegistration().get(registrationId);

        if (provider == null) {
            throw new Exception("不支持的provider registrationId:" + registrationId);
        }
        if (registration == null) {
            throw new Exception("不支持的registration registrationId:" + registrationId);
        }


        String state = UUID.fastUUID().toString();
        HashMap<String, String> param = new HashMap<>();

        String authorizationGrantType = registration.getAuthorizationGrantType();

        if (AuthorizationGrantType.AUTHORIZATION_CODE.getValue().equals(authorizationGrantType)) {
            OAuth2AuthorizationRequest authorizationRequest = OAuth2AuthorizationRequest.authorizationCode()
                    .clientId(registration.getClientId())
                    .authorizationUri(provider.getAuthorizationUri())
                    .scopes(registration.getScope())
                    .state(state)
                    .attributes(null)
                    .build();

            stateToDataMap.put(state, stateData);

            String authorizationRequestUri = authorizationRequest.getAuthorizationRequestUri();
            System.out.println(authorizationRequestUri);
            response.sendRedirect(authorizationRequestUri);
        } else {
            throw new Exception("不支持的 registration AuthorizationGrantType:" + authorizationGrantType);
        }
    }

}

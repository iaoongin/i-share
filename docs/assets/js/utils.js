/**
 * url 工具类
 */
const UrlUtils = {
    hash: {},
    query: {}
}

/**
 * 获取url参数对应值
 * @param {*} paramStr  url参数，如abc=3&qwe=1
 * @param {*} variable 如abc
 */
UrlUtils.getQueryVariable = function (paramStr, variable) {
    var vars = paramStr.split("&");
    for (var i = 0; i < vars.length; i++) {
        var pair = vars[i].split("=");
        if (pair[0] == variable) {
            return pair[1];
        }
    }
    return (false);
}

UrlUtils.hash.get = function (variable) {

    var paramStr = window.location.hash.substring(1)

    if (variable) {
        return UrlUtils.getQueryVariable(paramStr, variable)
    } else {
        return paramStr
    }

}

UrlUtils.query.get = function (variable) {

    var paramStr = window.location.query.substring(1)

    if (variable) {
        return UrlUtils.getQueryVariable(paramStr, variable)
    } else {
        return paramStr
    }

}

UrlUtils.hash.clean = function () {
    window.location.hash = ''
}



const TokenUtil = {}

TokenUtil.setAccessToken = function (token) {
    localStorage.setItem('access_token', token)
}

TokenUtil.getAccessToken = function () {
    return localStorage.getItem('access_token')
}
TokenUtil.setRefreshToken = function (token) {
    localStorage.setItem('refresh_token', token)
}

TokenUtil.getRefreshToken = function () {
    return localStorage.getItem('refresh_token')
}

TokenUtil.removeToken = function(){
    localStorage.removeItem('access_token')
    localStorage.removeItem('refresh_token')
}


const HttpUtil = {

}

HttpUtil.ajax = (method, url, headers, data, success, error) => {
    $.ajax({
        type: method,
        url: `${url}`,
        headers: {
            'Authorization': 'Bearer ' + TokenUtil.getAccessToken(),
            ...headers
        },
        data: data,
        success: (resp) => {
            if (resp.code === 200) {
                if (success) {
                    success(resp)
                }
            }else{
                alert(resp.msg)
            }

        },
        error: function (resp) {
            // alert(resp.status)
            console.log(resp)
            if (error) {
                error(resp)
            }

        }
    })
}

HttpUtil.post = (url, data, success) => {
    HttpUtil.ajax('post', url, {'Content-Type': "application/x-www-form-urlencoded"}, data, success)
}

HttpUtil.postJson = (url, data, success) => {
    HttpUtil.ajax('post', url, {'Content-Type': "application/json;charset=utf-8"}, JSON.stringify(data), success)
}

HttpUtil.get = (url, data, success) => {
    HttpUtil.ajax('get', url, {}, data, success)
}

HttpUtil.baseUrl = function (baseUrl) {

    return function () {

    }
}

export {
    UrlUtils,
    HttpUtil,
    TokenUtil
}
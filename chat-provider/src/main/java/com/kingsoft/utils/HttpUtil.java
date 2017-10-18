package com.kingsoft.utils;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HttpUtil {

    private static final Logger LOGGER = Logger.getLogger(HttpUtil.class);
    public static  final int TIMEOUT = 10000;
    public static String request(String url) {return request(url, null, null);}
    public static String request(String url, Map<String, String> p) {
        return request(url, p, null);
    }
    public static String postJson(String url, String json) {
        return postJson(url, json, null);
    }
    public static String postJson(String url, String json, Map<String, String> requestMap) {

        HttpClient client = getDefaultHttpClient(requestMap);
        HttpResponse response = null;
        HttpPost post = new HttpPost(url);
        post.addHeader("Content-type", "application/json;charset=utf-8");
        post.setHeader("Accept", "application/json");
        post.setEntity(new StringEntity(json, Charset.forName("utf-8")));
        try {
            response = client.execute(post);
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        return getString(url, client, response);
    }

    private static String getString(String url, HttpClient client, HttpResponse response) {

        String retString = null;
        if (response == null) {
            LOGGER.warn("url:" + url + " network request wrong.");
            return null;
        }
        try {
            HttpEntity entity = response.getEntity();
            if (entity == null) {
                return retString;
            }
            retString = EntityUtils.toString(entity);
            //统一处理
            LOGGER.warn(retString);
            return retString;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (client != null) {
                client.getConnectionManager().shutdown();
                client = null;
            }
        }
        return retString;
    }


    /**
     *
     * @param url,
     * @param parameters post请求使用的参数
     * @param requestMap http的相关参数
     * @return
     */
    public static String request(String url, Map<String, String> parameters, Map<String, String> requestMap) {
        HttpClient client = getDefaultHttpClient(requestMap);
        String retString = null;
        HttpResponse response = null;
        if(parameters == null || parameters.size() == 0) {
            HttpGet get = new HttpGet(url);
            try {
                response = client.execute(get);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            HttpPost post = new HttpPost(url);
            List<NameValuePair> params=new ArrayList<NameValuePair>();
            for(Map.Entry<String, String> entry : parameters.entrySet()) {
                params.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            }
            try {
                post.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
                response = client.execute(post);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                LOGGER.info(e.getMessage());
            }

        }
        return getString(url, client, response);
    }

    private static HttpClient getDefaultHttpClient(Map<String, String> requestMap) {
        int timeout = 10000;
        int soTimeout = 10000;
        if (requestMap != null) {

            timeout = getTimeout(requestMap, "timeout");
            soTimeout = getTimeout(requestMap, "soTimeOut");
        }

        RequestConfig defaultRequestConfig = RequestConfig.custom()
                .setSocketTimeout(soTimeout)
                .setConnectTimeout(timeout)
                .setConnectionRequestTimeout(timeout)
                .setStaleConnectionCheckEnabled(true)
                .build();

        CloseableHttpClient httpclient = HttpClients.custom()
                .setDefaultRequestConfig(defaultRequestConfig)
                .build();

        return httpclient;
    }

    private static int getTimeout(Map<String, String> requestMap, String key) {

        String value = requestMap.get(key);
        if (StringUtils.isEmpty(value)) {
            return TIMEOUT;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {

            e.printStackTrace();
            return TIMEOUT;
        }
    }


}

package com.example.demohttpclient.commons;

import org.apache.http.*;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpClientUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpClientUtil.class);

    protected static CloseableHttpClient client = HttpClientPool.getHttpclient();

    protected static Header getBasicAuthenHeader(String userAuth, String passAuth) {
        return new BasicHeader(HttpHeaders.AUTHORIZATION, "Basic " + getCredentials(userAuth, passAuth));
    }

    private static String getCredentials(String userAuth, String passAuth) {
        return Base64.getEncoder().encodeToString((userAuth + ":" + passAuth).getBytes());
    }

    public static String getRequest(String url) throws Exception {
        return getRequest(url, null, null, null);
    }

    public static String getRequest(String url, String userAuth, String passAuth) throws Exception {
        return getRequest(url, userAuth, passAuth, null);
    }

    public static String getRequest(String url, Map<String, String> headers) throws Exception {
        return getRequest(url, null, null, headers);
    }

    public static String getRequest(String url, String userAuth, String passAuth, Map<String, String> headers)
            throws Exception {
        CloseableHttpResponse response = null;
        StringBuilder returnString = new StringBuilder();
        try {
            HttpGet httpget = new HttpGet(url);
            if (userAuth != null && passAuth != null) {
                httpget.addHeader(getBasicAuthenHeader(userAuth, passAuth));
            }
            if (headers != null) {
                headers.forEach(httpget::addHeader);
            }
            response = client.execute(httpget);
            try (BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()))) {
                String inputLine = "";
                while ((inputLine = rd.readLine()) != null) {
                    returnString.append(inputLine);
                }
            }
            return returnString.toString();
        } catch (Exception ex) {
            LOGGER.error("Get request to url {} has error:{}", url, ex.getMessage());
            throw ex;
        } finally {
            response.close();
        }
    }

    public static String getRequestByToken(String url, String token, Map<String, String> headers) throws Exception {
        {
            CloseableHttpResponse response = null;
            StringBuilder returnString = new StringBuilder();
            try {
                HttpGet httpget = new HttpGet(url);
                if (headers == null) {
                    headers = new HashMap<>();
                }
                headers.put(HttpHeaders.AUTHORIZATION, "Bearer " + token);

                if (headers != null) {
                    headers.forEach(httpget::addHeader);
                }

                response = client.execute(httpget);
                try (BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()))) {
                    String inputLine = "";
                    while ((inputLine = rd.readLine()) != null) {
                        returnString.append(inputLine);
                    }
                }
                if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    return returnString.toString();
                }
                throw new Exception(returnString.toString());
            } catch (Exception ex) {
                LOGGER.error("Get request to url {} has error:{}", url, ex.getMessage());
                throw ex;
            } finally {
                response.close();
            }
        }
    }

    public static String postRequest(String url, Object obj) throws Exception {
        return postRequest(url, null, null, obj);
    }

    public static String postRequest(String url, String userAuth, String passAuth, Object obj) throws Exception {
        return postRequest(url, userAuth, passAuth, obj, null);
    }

    public static String postRequest(String url, Object obj, Map<String, String> headers) throws Exception {
        return postRequest(url, null, null, obj, headers);
    }

    public static String postRequest(String url, String userAuth, String passAuth, Object obj,
                                     Map<String, String> headers) throws Exception {
        CloseableHttpResponse response = null;
        String returnString = "";
        JsonMapperUtil jsonUtil = new JsonMapperUtil();
        try {
            HttpPost httpPost = new HttpPost(url);
            if (userAuth != null && passAuth != null) {
                httpPost.addHeader(getBasicAuthenHeader(userAuth, passAuth));
            }
            if (obj != null) {

                String json = jsonUtil.mapToJson(obj);
                StringEntity entity = new StringEntity(json);
                httpPost.setEntity(entity);
            }
            if (headers != null) {
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    httpPost.addHeader(entry.getKey(), entry.getValue());
                }
            }

            httpPost.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
            httpPost.setHeader(HttpHeaders.ACCEPT, "application/json");
            response = client.execute(httpPost);
            try (BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()))) {
                String inputLine = "";
                while ((inputLine = rd.readLine()) != null) {
                    returnString += inputLine;
                }
            }
            return returnString;

        } catch (Exception ex) {
            LOGGER.error("ERR when invoke  :" + ex.getMessage());
            throw ex;
        } finally {
            response.close();
        }
    }

    public static String postRequestByToken(String url, String token, Object obj, Map<String, String> headers)
            throws Exception {
        CloseableHttpResponse response = null;
        StringBuilder returnString = new StringBuilder();
        JsonMapperUtil jsonUtil = new JsonMapperUtil();
        try {
            HttpPost httpPost = new HttpPost(url);
            if (headers == null) {
                headers = new HashMap<>();
            }
            headers.put(HttpHeaders.AUTHORIZATION, "Bearer " + token);
            if (obj != null) {

                String json = jsonUtil.mapToJson(obj);
                StringEntity entity = new StringEntity(json);
                httpPost.setEntity(entity);
            }
            if (headers != null) {
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    httpPost.addHeader(entry.getKey(), entry.getValue());
                }
            }

            httpPost.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
            httpPost.setHeader(HttpHeaders.ACCEPT, "application/json");
            response = client.execute(httpPost);
            try (BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()))) {
                String inputLine = "";
                while ((inputLine = rd.readLine()) != null) {
                    returnString.append(inputLine);
                }
            }
            return returnString.toString();

        } catch (Exception ex) {
            LOGGER.error("ERR when invoke  :" + ex.getMessage());
            throw ex;
        } finally {
            response.close();
        }
    }

    public static String postRequestFormData(String url, List<NameValuePair> form) throws Exception {
        return postRequestFormData(url, null, null, form, null);
    }

    public static String postRequestFormData(String url, List<NameValuePair> form, Map<String, String> headers)
            throws Exception {
        return postRequestFormData(url, null, null, form, headers);
    }

    public static String postRequestFormData(String url, String userAuth, String passAuth, List<NameValuePair> form,
                                             Map<String, String> headers) throws Exception {
        CloseableHttpResponse response = null;
        String returnString = "";
        JsonMapperUtil jsonUtil = new JsonMapperUtil();
        try {
            HttpPost httpPost = new HttpPost(url);
            if (userAuth != null && passAuth != null) {
                httpPost.addHeader(getBasicAuthenHeader(userAuth, passAuth));
            }
            if (headers != null) {
                headers.forEach(httpPost::addHeader);
            }
            if (form != null) {
                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(form, Consts.UTF_8);
                httpPost.setEntity(entity);
            }
            response = client.execute(httpPost);

            try (BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()))) {
                String inputLine = "";
                while ((inputLine = rd.readLine()) != null) {
                    returnString += inputLine;
                }
            }
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                return returnString;
            }
            throw new Exception(returnString);
        } catch (Exception ex) {
            throw ex;
        } finally {
            response.close();
        }
    }
}

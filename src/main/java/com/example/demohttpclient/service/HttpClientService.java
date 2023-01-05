package com.example.demohttpclient.service;

import com.example.demohttpclient.commons.HttpClientUtil;
import org.springframework.stereotype.Service;

@Service
public class HttpClientService implements IHttpClientService{

    @Override
    public String callApi(String url) {
        try {
            return HttpClientUtil.getRequest(url);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

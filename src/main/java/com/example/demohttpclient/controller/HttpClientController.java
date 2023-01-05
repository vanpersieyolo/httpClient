package com.example.demohttpclient.controller;

import com.example.demohttpclient.service.IHttpClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/http")
public class HttpClientController {
    private final IHttpClientService iHttpClientService;

    @GetMapping(value = "/call")
    public ResponseEntity<?> callApi(@RequestParam(value = "url") String url){
        return new ResponseEntity<>(iHttpClientService.callApi(url), HttpStatus.OK);
    }
}

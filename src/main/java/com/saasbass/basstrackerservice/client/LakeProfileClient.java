package com.saasbass.basstrackerservice.client;

import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

public class LakeProfileClient {
    private RestTemplate restTemplate;
    private String lakeProfileServiceBaseUrl;

    public LakeProfileClient(HttpComponentsClientHttpRequestFactory clientFactory,
                             String lakeProfileServiceBaseUrl) {
        this.restTemplate = new RestTemplate(clientFactory);
        this.lakeProfileServiceBaseUrl = lakeProfileServiceBaseUrl;
    }

    public LakeProfile getLakeProfile(Long id) {
        String url = lakeProfileServiceBaseUrl + "/lake-profile/" + id;
        return restTemplate.getForObject(url, LakeProfile.class);
    }

    public void createLakeProfile(LakeProfile lakeProfile) {
        String url = lakeProfileServiceBaseUrl + "/lake-profile";
        restTemplate.postForObject(url, lakeProfile, LakeProfile.class);
    }
}

package com.saasbass.basstrackerservice.client;

import com.saasbass.basstrackerservice.BassTrackerServiceApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

public class LakeProfileClient {
    private RestTemplate restTemplate;
    private String lakeProfileServiceBaseUrl;
    private static final Logger log = LoggerFactory.getLogger(BassTrackerServiceApplication.class);

    public LakeProfileClient(HttpComponentsClientHttpRequestFactory clientFactory,
                             String lakeProfileServiceBaseUrl) {
        this.restTemplate = new RestTemplate(clientFactory);
        this.lakeProfileServiceBaseUrl = lakeProfileServiceBaseUrl;
    }

    public LakeProfile getLakeProfile(Long id) {
        String url = lakeProfileServiceBaseUrl + "/lake-profile/" + id;
        log.info("--- Get lake profile attempt ---");
        return restTemplate.getForObject(url, LakeProfile.class);
    }

    public void createLakeProfile(LakeProfile lakeProfile) {
        String url = lakeProfileServiceBaseUrl + "/lake-profile";
        log.info("--- Create lake profile attempt ---");
        restTemplate.postForObject(url, lakeProfile, LakeProfile.class);
    }
}

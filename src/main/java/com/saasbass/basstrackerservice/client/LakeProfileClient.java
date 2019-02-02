package com.saasbass.basstrackerservice.client;

import com.saasbass.basstrackerservice.BassTrackerServiceApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.web.client.RestTemplate;

public class LakeProfileClient {
    private RestTemplate restTemplate;
    private String lakeProfileServiceBaseUrl;
    private RetryTemplate retryTemplate;
    private static final Logger log = LoggerFactory.getLogger(BassTrackerServiceApplication.class);

    public LakeProfileClient(HttpComponentsClientHttpRequestFactory clientFactory,
                             String lakeProfileServiceBaseUrl,
                             RetryTemplate retryTemplate) {
        this.restTemplate = new RestTemplate(clientFactory);
        this.lakeProfileServiceBaseUrl = lakeProfileServiceBaseUrl;
        this.retryTemplate = retryTemplate;
    }

    public LakeProfile getLakeProfile(Long id) {
        String url = lakeProfileServiceBaseUrl + "/lake-profile/" + id;
        return retryTemplate.execute(context -> {
            log.info("--- Get lake profile attempt ---");
            return restTemplate.getForObject(url, LakeProfile.class);
        });
    }

    public void createLakeProfile(LakeProfile lakeProfile) {
        String url = lakeProfileServiceBaseUrl + "/lake-profile";
        retryTemplate.execute(context -> {
            log.info("--- Create lake profile attempt ---");
            return restTemplate.postForObject(url, lakeProfile, LakeProfile.class);
        });
    }
}

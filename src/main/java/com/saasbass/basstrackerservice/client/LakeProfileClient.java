package com.saasbass.basstrackerservice.client;

import com.saasbass.basstrackerservice.BassTrackerServiceApplication;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.HttpHostConnectException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.net.SocketTimeoutException;

public class LakeProfileClient {
    private RestTemplate restTemplate;
    private String lakeProfileServiceBaseUrl;
    private RetryTemplate retryTemplate;
    private static final Logger log = LoggerFactory.getLogger(BassTrackerServiceApplication.class);
    private long lastMillis = System.currentTimeMillis();
    private boolean isFirstRequest = true;

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
            long currentTime = System.currentTimeMillis();
            log.info("--- Get lake profile attempt ---" + (!isFirstRequest ?
                    " backoff duration: " + (currentTime - lastMillis) + " milliseconds" : ""));
            lastMillis = currentTime;
            isFirstRequest = false;

            LakeProfile lakeProfile = null;
            try {
                lakeProfile = restTemplate.getForObject(url, LakeProfile.class);
            }
            catch (ResourceAccessException e) {
                if (e.getCause() instanceof HttpHostConnectException) {
                    throw e;
                }
                if (e.getCause() instanceof ConnectTimeoutException) {
                    throw e;
                }
                if (e.getCause() instanceof SocketTimeoutException) {
                    //log.error("--- Error: Took too long to get lake profile! ---");
                    throw e;
                }
            }
            return lakeProfile;
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

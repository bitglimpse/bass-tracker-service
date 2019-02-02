package com.saasbass.basstrackerservice;

import com.saasbass.basstrackerservice.client.LakeProfile;
import com.saasbass.basstrackerservice.client.LakeProfileClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.retry.support.RetryTemplate;

@SpringBootApplication
public class BassTrackerServiceApplication {

    private static final Logger log = LoggerFactory.getLogger(BassTrackerServiceApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(BassTrackerServiceApplication.class, args);
    }

    @Bean
    RetryTemplate retryTemplate() {
        return new RetryTemplate();
    }

    @Bean
    public LakeProfileClient lakeProfileClient(@Value("${lakeProfileServiceBaseUrl}") String lakeProfileServiceBaseUrl,
                                               @Value("${connectTimeout}") int clientConnectTimeout,
                                               @Value("${readTimeout}") int clientReadTimeout,
                                               RetryTemplate retryTemplate) {
        return new LakeProfileClient(clientFactory(clientConnectTimeout, clientReadTimeout),
                lakeProfileServiceBaseUrl, retryTemplate);
    }

    private HttpComponentsClientHttpRequestFactory clientFactory(int clientConnectTimeout, int clientReadTimeout) {
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setReadTimeout(clientReadTimeout);
        factory.setConnectTimeout(clientConnectTimeout);
        return factory;
    }

    @Bean
    public CommandLineRunner run(LakeProfileClient lakeProfileClient) {
        return args -> {
            new Thread(() -> {
                lakeProfileClient.createLakeProfile(
                        new LakeProfile(1L, "Strawberry Reservoir", "Utah", 40.175397, -111.102157));
                log.info("--- Success! Create lake profile attempt was successful ---");
            }).start();

            Thread.sleep(3000);

            LakeProfile strawberryLake = lakeProfileClient.getLakeProfile(1L);
            log.info(strawberryLake.toString());
        };
    }
}

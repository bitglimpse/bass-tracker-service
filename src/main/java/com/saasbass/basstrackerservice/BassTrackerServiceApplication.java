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
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
public class BassTrackerServiceApplication {

    private static final Logger log = LoggerFactory.getLogger(BassTrackerServiceApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(BassTrackerServiceApplication.class, args);
    }

    @Bean
    RetryTemplate retryTemplate(@Value("${retry.fixed.backoffPeriod}") int fixedBackoffPeriod,
                                @Value("${retry.exponential.initialBackoffInterval}") int expInitialBackoffInterval,
                                @Value("${retry.exponential.maxBackoffInterval}") int expMaxBackoffInterval,
                                @Value("${retry.exponential.backoffMultiplier}") double expBackoffMultiplier,
                                @Value("${retry.maxAttempts}") int maxRetryAttempts) {

        Map<Class<? extends Throwable>, Boolean> retryableExceptions = new HashMap<>();
        retryableExceptions.put(RestClientException.class, true); // HTTP 404, 503, etc.

        // Retry exception blacklist
        retryableExceptions.put(HttpClientErrorException.BadRequest.class, false); // HTTP 400
        retryableExceptions.put(HttpServerErrorException.InternalServerError.class, false); // HTTP 500

        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy(maxRetryAttempts, retryableExceptions);

        FixedBackOffPolicy fixedBackOffPolicy = new FixedBackOffPolicy();
        fixedBackOffPolicy.setBackOffPeriod(fixedBackoffPeriod);

        ExponentialBackOffPolicy exponentialBackOffPolicy = new ExponentialBackOffPolicy();
        exponentialBackOffPolicy.setInitialInterval(expInitialBackoffInterval);
        exponentialBackOffPolicy.setMaxInterval(expMaxBackoffInterval);
        exponentialBackOffPolicy.setMultiplier(expBackoffMultiplier);

        RetryTemplate template = new RetryTemplate();
        template.setRetryPolicy(retryPolicy);
        template.setBackOffPolicy(exponentialBackOffPolicy);

        return template;
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

package com.saasbass.basstrackerservice;

import com.saasbass.basstrackerservice.client.LakeProfile;
import com.saasbass.basstrackerservice.client.LakeProfileClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class BassTrackerServiceApplication {

    private static final Logger log = LoggerFactory.getLogger(BassTrackerServiceApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(BassTrackerServiceApplication.class, args);
    }

    @Bean
    public LakeProfileClient lakeProfileClient(RestTemplateBuilder builder,
                                               @Value("${lakeProfileServiceBaseUrl}") String lakeProfileServiceBaseUrl) {
        return new LakeProfileClient(builder, lakeProfileServiceBaseUrl);
    }

    @Bean
    public CommandLineRunner run(LakeProfileClient lakeProfileClient) throws Exception {
        return args -> {
            new Thread(() -> {
                lakeProfileClient.createLakeProfile(
                        new LakeProfile(1L, "Strawberry Reservoir", "Utah", 40.175397, -111.102157));
            }).start();

            // This simulates a resource race condition caused by the GET request
            // for a resource executing before the POST request has created the resource.
            //Thread.sleep(3000);

            LakeProfile strawberryLake = lakeProfileClient.getLakeProfile(1L);
            log.info(strawberryLake.toString());
        };
    }
}

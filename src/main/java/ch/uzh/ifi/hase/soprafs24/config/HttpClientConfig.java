package ch.uzh.ifi.hase.soprafs24.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;


import java.net.http.HttpClient;

@Configuration
public class HttpClientConfig {

    @Bean
    @Primary
    public HttpClient httpClient() {
        return HttpClient.newHttpClient();
    }
}
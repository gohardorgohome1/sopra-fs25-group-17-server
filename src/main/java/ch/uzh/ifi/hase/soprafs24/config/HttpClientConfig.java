package ch.uzh.ifi.hase.soprafs24.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Qualifier;

import java.net.http.HttpClient;

@Configuration
public class HttpClientConfig {

    @Bean
    @Qualifier("javaNetHttpClient")
    public HttpClient httpClient() {
        return HttpClient.newHttpClient();
    }
}


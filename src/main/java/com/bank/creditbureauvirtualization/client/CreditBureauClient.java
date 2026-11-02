package com.bank.creditbureauvirtualization.client;

import com.bank.creditbureauvirtualization.model.CreditScoreResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;

@Component
public class CreditBureauClient {

    private final RestClient restClient;

    public CreditBureauClient() {
        SimpleClientHttpRequestFactory factory =
                new SimpleClientHttpRequestFactory();

        factory.setConnectTimeout(Duration.ofSeconds(2));
        factory.setReadTimeout(Duration.ofSeconds(2));

        this.restClient = RestClient.builder()
                .baseUrl("http://localhost:8081")
                .requestFactory(factory)
                .build();
    }


    public CreditScoreResponse getCreditScore(String customerId) {
        return getCreditScore(customerId, "normal");
    }

    public CreditScoreResponse getCreditScore(
            String customerId,
            String scenario
    ) {
        String path = switch (scenario) {
            case "server-error" ->
                    "/credit-score-error/" + customerId;
            case "timeout" ->
                    "/credit-score-timeout/" + customerId;
            default ->
                    "/credit-score/" + customerId;
        };

        try {
            return restClient.get()
                    .uri(path)
                    .retrieve()
                    .body(CreditScoreResponse.class);

        } catch (HttpClientErrorException.NotFound ex) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Customer record not found in credit bureau"
            );

        } catch (HttpServerErrorException ex) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "Credit bureau returned a server error"
            );

        } catch (ResourceAccessException ex) {
            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "Credit bureau is unavailable or timed out"
            );
        }
    }

}
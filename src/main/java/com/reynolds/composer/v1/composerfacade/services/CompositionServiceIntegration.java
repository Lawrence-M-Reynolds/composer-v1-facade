package com.reynolds.composer.v1.composerfacade.services;

import com.reynolds.composer.v1.api.core.composition.composition.Composition;
import com.reynolds.composer.v1.api.core.composition.composition.CompositionController;
import com.reynolds.composer.v1.api.core.composition.composition.generated.CompositionVariation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.List;

import static org.springframework.http.HttpMethod.GET;

@Component
public class CompositionServiceIntegration implements CompositionController {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final WebClient webClient;

    public CompositionServiceIntegration(WebClient.Builder webClient,
                                         @Value("${app.composition-service.host}") String serviceHost,
                                         @Value("${app.composition-service.port}") int servicePort, RestTemplate restTemplate) {
        String serviceUrl = "http://" + serviceHost + ":" + servicePort + "/compositions";
        this.webClient = webClient.baseUrl(serviceUrl).build();
    }

    @Override
    public Mono<Composition> getComposition(long compositionId) {
        WebClient.UriSpec<WebClient.RequestBodySpec> uriSpec = webClient.method(GET);
        WebClient.RequestBodySpec bodySpec = uriSpec.uri(uriBuilder -> uriBuilder
                .pathSegment(Long.toString(compositionId))
                .build());
        return bodySpec.exchangeToMono(response -> {
            if (response.statusCode() == HttpStatus.NOT_FOUND) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Composition not found");
            }
            return response.bodyToMono(Composition.class);
        });
    }

//    @Override
//    public Mono<Composition> getComposition(long compositionId) {
//        return webClient.get().uri(uriBuilder -> uriBuilder
//                        .pathSegment(Long.toString(compositionId))
//                        .build())
//                .retrieve()
//                .onStatus(httpStatusCode -> HttpStatus.NOT_FOUND == httpStatusCode, response -> Mono.empty())
//                .bodyToMono(Composition.class);
//    }

    @Override
    public Flux<Composition> getCompositions() {
        return webClient.get()
                .retrieve()
                .bodyToFlux(Composition.class);
    }

    @Override
    public Mono<Composition> save(Composition composition) throws IOException {
        return webClient.post().uri(uriBuilder -> uriBuilder
                        .path("/save")
                        .build()
                )
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(composition), Composition.class)
                .retrieve()
                .bodyToMono(Composition.class);
    }
}

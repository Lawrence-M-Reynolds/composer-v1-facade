package com.reynolds.composer.v1.composerfacade.services;

import com.reynolds.composer.v1.api.core.composition.composition.generated.CompositionVariation;
import com.reynolds.composer.v1.api.core.generator.generator.GeneratorController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.List;

@Component
public class GenerationServiceIntegration implements GeneratorController {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final WebClient webClient;

    @Autowired
    public GenerationServiceIntegration (WebClient.Builder webClient,
                                @Value("${app.generation-service.host}") String serviceHost,
                                @Value("${app.generation-service.port}") int servicePort)
    {
        String serviceUrl = "http://" + serviceHost + ":" + servicePort + "/generator";
        this.webClient = webClient.baseUrl(serviceUrl).build();
    }

    @Override
    public Mono<List<String>> processComposition(long compositionId) throws IOException {
        logger.debug("Generating composition " + compositionId);

        return webClient.post().uri(uriBuilder -> uriBuilder
                .path("/process")
                .queryParam("compositionId", Long.toString(compositionId))
                .build())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<String>>() {});
    }

    @Override
    public Mono<Integer> getGeneratedCountForComposition(long compositionId) throws IOException {
        return webClient.get().uri(uriBuilder -> uriBuilder
                                .path("/getGeneratedCount")
                                .pathSegment(Long.toString(compositionId))
                                .build())
                .retrieve()
                .bodyToMono(Integer.class);
    }

    @Override
    public Flux<CompositionVariation> getGeneratedVariationsForComposition(long compositionId) throws IOException {
        return webClient.get().uri(uriBuilder -> uriBuilder
                        .path("/getGeneratedVariations")
                        .pathSegment(Long.toString(compositionId))
                        .build())
                .retrieve()
                .bodyToFlux(CompositionVariation.class);
    }
}

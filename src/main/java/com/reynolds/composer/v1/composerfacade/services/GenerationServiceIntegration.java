package com.reynolds.composer.v1.composerfacade.services;

import com.reynolds.composer.v1.api.core.composition.composition.generated.CompositionVariation;
import com.reynolds.composer.v1.api.core.generator.generator.GeneratorController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.springframework.http.HttpMethod.GET;

@Component
public class GenerationServiceIntegration implements GeneratorController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final String compostionServiceUrl;
    private RestTemplate restTemplate;

    public GenerationServiceIntegration (RestTemplate restTemplate,
                                @Value("${app.generation-service.host}") String serviceHost,
                                @Value("${app.generation-service.port}") int servicePort) {

        this.restTemplate = restTemplate;
        compostionServiceUrl = "http://" + serviceHost + ":" + servicePort + "/generator";
    }

    @Override
    public ResponseEntity<Void> processComposition(long compositionId) throws IOException {
        logger.debug("Generating composition " + compositionId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map= new LinkedMultiValueMap<>();
        map.add("compositionId", Long.toString(compositionId));

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        return restTemplate.postForEntity(compostionServiceUrl + "/process", request, Void.class);
    }

    @Override
    public int getGeneratedCountForComposition(long compositionId) throws IOException {
        String url = compostionServiceUrl + "/getGeneratedCount/" + compositionId;
        logger.debug("Calling URL: {}", url);
        String countString = restTemplate.getForObject(url, String.class);
        return Optional.ofNullable(countString).map(Integer::parseInt).orElse(0);
    }

    @Override
    public List<CompositionVariation> getGeneratedVariationsForComposition(long compositionId) throws IOException {
        String url = compostionServiceUrl + "/getGeneratedVariations/" + compositionId;
        logger.debug("Calling URL: {}", url);
        return restTemplate.exchange(url, GET, null,
                new ParameterizedTypeReference<List<CompositionVariation>>() {}).getBody();
    }
}

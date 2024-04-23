package com.reynolds.composer.v1.composerfacade.services;

import static org.springframework.http.HttpMethod.GET;

import com.reynolds.composer.v1.api.core.composition.composition.Composition;
import com.reynolds.composer.v1.api.core.composition.composition.CompositionController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;

@Component
public class ComposerIntegration implements CompositionController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final String compostionServiceUrl;
    private RestTemplate restTemplate;

    public ComposerIntegration (RestTemplate restTemplate,
                                @Value("${app.composition-service.host}") String compostionServiceHost,
                                @Value("${app.composition-service.port}") int compositionServicePort) {

        this.restTemplate = restTemplate;
        compostionServiceUrl = "http://" + compostionServiceHost + ":" + compositionServicePort + "/compositions";
    }

    @Override
    public String testEndpoint() {
        String url = compostionServiceUrl + "/test";
        logger.debug("Calling URL: {}", url);
        return restTemplate.getForObject(url, String.class);
    }

    @Override
    public List<Composition> getCompositions() {
        return restTemplate.exchange(compostionServiceUrl, GET, null,
                new ParameterizedTypeReference<List<Composition>>() {}).getBody();
    }

    @Override
    public ResponseEntity<Composition> save(Composition composition) throws IOException {
        return restTemplate.postForEntity(compostionServiceUrl + "/save", composition, Composition.class);
    }
}

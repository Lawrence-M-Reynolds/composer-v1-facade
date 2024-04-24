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
public class CompositionServiceIntegration implements CompositionController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final String compostionServiceUrl;
    private RestTemplate restTemplate;

    public CompositionServiceIntegration(RestTemplate restTemplate,
                                         @Value("${app.composition-service.host}") String serviceHost,
                                         @Value("${app.composition-service.port}") int servicePort) {

        this.restTemplate = restTemplate;
        compostionServiceUrl = "http://" + serviceHost + ":" + servicePort + "/compositions";
    }

    @Override
    public ResponseEntity<Composition> getComposition(long compositionId) {
        String url = compostionServiceUrl + "/" + compositionId;
        return restTemplate.exchange(url, GET, null, Composition.class);
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

package com.reynolds.composer.v1.composerfacade.services;

import com.reynolds.composer.v1.api.core.composition.composition.Composition;
import com.reynolds.composer.v1.api.core.generator.generator.GeneratorController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

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

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);

        return restTemplate.postForEntity(compostionServiceUrl + "/process", request, Void.class);
    }

}

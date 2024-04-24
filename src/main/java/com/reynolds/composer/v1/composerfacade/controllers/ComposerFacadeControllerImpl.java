package com.reynolds.composer.v1.composerfacade.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reynolds.composer.v1.api.composerFacade.ComposerFacadeController;
import com.reynolds.composer.v1.api.core.composition.composition.Composition;
import com.reynolds.composer.v1.composerfacade.services.ComposerIntegration;
import com.reynolds.composer.v1.composerfacade.services.GenerationServiceIntegration;
import com.reynolds.composer.v1.util.http.ServiceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
public class ComposerFacadeControllerImpl implements ComposerFacadeController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private ServiceUtil serviceUtil;
    private ComposerIntegration composerIntegration;
    private GenerationServiceIntegration generationServiceIntegration;

    public ComposerFacadeControllerImpl(ServiceUtil serviceUtil,
                                        ComposerIntegration composerIntegration,
                                        GenerationServiceIntegration generationServiceIntegration) {
        this.serviceUtil = serviceUtil;
        this.composerIntegration = composerIntegration;
        this.generationServiceIntegration = generationServiceIntegration;
    }

    @Override
    public String getComposition() {
        return "Working! getServiceAddress: " + serviceUtil.getServiceAddress();
    }

    @Override
    public ResponseEntity<Composition> uploadFile (@RequestParam("file") MultipartFile file) throws IOException {
        Composition composition = new ObjectMapper().readValue(file.getInputStream(), Composition.class);
        return composerIntegration.save(composition);
    }

    @Override
    public ResponseEntity<Void> processComposition(long compositionId) throws IOException {
        return generationServiceIntegration.processComposition(compositionId);
    }

    @Override
    public List<Composition> getCompositions() {
        return composerIntegration.getCompositions();
    }

    @Override
    public String testEndpoint() {
        return composerIntegration.testEndpoint();
    }
}

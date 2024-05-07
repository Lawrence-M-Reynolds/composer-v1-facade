package com.reynolds.composer.v1.composerfacade.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reynolds.composer.v1.api.composerFacade.ComposerFacadeController;
import com.reynolds.composer.v1.api.core.composition.composition.Composition;
import com.reynolds.composer.v1.api.core.composition.composition.generated.CompositionVariation;
import com.reynolds.composer.v1.composerfacade.services.CompositionServiceIntegration;
import com.reynolds.composer.v1.composerfacade.services.GenerationServiceIntegration;
import com.reynolds.composer.v1.util.http.ServiceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
public class ComposerFacadeControllerImpl implements ComposerFacadeController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ServiceUtil serviceUtil;
    private final CompositionServiceIntegration compositionServiceIntegration;
    private final GenerationServiceIntegration generationServiceIntegration;

    public ComposerFacadeControllerImpl(ServiceUtil serviceUtil,
                                        CompositionServiceIntegration compositionServiceIntegration,
                                        GenerationServiceIntegration generationServiceIntegration) {
        this.serviceUtil = serviceUtil;
        this.compositionServiceIntegration = compositionServiceIntegration;
        this.generationServiceIntegration = generationServiceIntegration;
    }

    @Override
    public ResponseEntity<Composition> getComposition(@PathVariable("compositionId") int compositionId) {
        return compositionServiceIntegration.getComposition(compositionId);
    }

    @Override
    public ResponseEntity<Composition> uploadFile (@RequestParam("file") MultipartFile file) throws IOException {
        Composition composition = new ObjectMapper().readValue(file.getInputStream(), Composition.class);
        return compositionServiceIntegration.save(composition);
    }

    @Override
    public ResponseEntity<Void> processComposition(long compositionId) throws IOException {
        return generationServiceIntegration.processComposition(compositionId);
    }

    @Override
    public int getGeneratedCount(long compositionId) throws IOException {
        return generationServiceIntegration.getGeneratedCountForComposition(compositionId);
    }

    @Override
    public List<CompositionVariation> getGeneratedVariations(long compositionId) throws IOException {
        return generationServiceIntegration.getGeneratedVariationsForComposition(compositionId);
    }

    @Override
    public List<Composition> getCompositions() {
        return compositionServiceIntegration.getCompositions();
    }

    @Override
    public String testEndpoint() {
        return compositionServiceIntegration.testEndpoint();
    }
}

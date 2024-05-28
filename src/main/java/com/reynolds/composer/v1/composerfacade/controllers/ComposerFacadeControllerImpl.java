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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.List;

@RestController
public class ComposerFacadeControllerImpl implements ComposerFacadeController {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

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
    public Mono<Composition> getComposition(@PathVariable("compositionId") int compositionId) {
        return compositionServiceIntegration.getComposition(compositionId);
    }

    @Override
    public Mono<Composition> uploadFile (@RequestParam("file") MultipartFile file) throws IOException {
        Composition composition = new ObjectMapper().readValue(file.getInputStream(), Composition.class);
        return compositionServiceIntegration.save(composition);
    }

    @Override
    public Mono<List<String>> processComposition(long compositionId) throws IOException {
        return generationServiceIntegration.processComposition(compositionId);
    }

    @Override
    public Mono<Integer> getGeneratedCount(long compositionId) throws IOException {
        return generationServiceIntegration.getGeneratedCountForComposition(compositionId);
    }

    @Override
    public Flux<CompositionVariation> getGeneratedVariations(long compositionId) throws IOException {
        return generationServiceIntegration.getGeneratedVariationsForComposition(compositionId);
    }

    @Override
    public Flux<Composition> getCompositions() {
        return compositionServiceIntegration.getCompositions();
    }
}

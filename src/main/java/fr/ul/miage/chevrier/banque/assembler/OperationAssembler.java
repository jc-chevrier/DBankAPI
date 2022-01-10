package fr.ul.miage.chevrier.banque.assembler;

import fr.ul.miage.chevrier.banque.controller.OperationController;
import fr.ul.miage.chevrier.banque.dto.OperationView;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * Classe pour associer aux vues (DTOs) des
 * opérations bancaires des liens d'actions
 * (HATEOAS).
 */
@Component
public class OperationAssembler implements RepresentationModelAssembler<OperationView, EntityModel<OperationView>> {
    @Override
    public EntityModel<OperationView> toModel(OperationView operationView) {
        return EntityModel.of(operationView,
                linkTo(methodOn(OperationController.class)
                        .findAll(null, null, null, null,null, null, null,
                                 null, null, null, null, null, null, null))
                        .withRel("collection"),
                linkTo(methodOn(OperationController.class)
                        .find(operationView.getId()))
                        .withSelfRel());
    }

    @Override
    public CollectionModel<EntityModel<OperationView>> toCollectionModel(Iterable<? extends OperationView> operationsViews) {
        List<EntityModel<OperationView>> operationModel = StreamSupport.stream(operationsViews.spliterator(), false)
                .map(this::toModel)
                .collect(Collectors.toList());
        return CollectionModel.of(operationModel,
                linkTo(methodOn(OperationController.class)
                        .findAll(null, null, null, null, null, null, null,
                                 null, null, null, null, null, null, null))
                        .withSelfRel());
    }
}
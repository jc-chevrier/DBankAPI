package fr.ul.miage.chevrier.dbank_api.assembler;

import fr.ul.miage.chevrier.dbank_api.controller.CardController;
import fr.ul.miage.chevrier.dbank_api.dto.CardView;
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
 * cartes bancaires des liens d'actions (HATEOAS).
 */
@Component
public class CardAssembler implements RepresentationModelAssembler<CardView, EntityModel<CardView>> {
    @Override
    public EntityModel<CardView> toModel(CardView cardView) {
        return EntityModel.of(cardView,
                linkTo(methodOn(CardController.class)
                        .findAll(null, null, null, null, null,  null, null, null, null, null, null, null, null, null))
                        .withRel("collection"),
                linkTo(methodOn(CardController.class)
                        .find(cardView.getId()))
                        .withSelfRel());
    }

    @Override
    public CollectionModel<EntityModel<CardView>> toCollectionModel(Iterable<? extends CardView> cardsViews) {
        List<EntityModel<CardView>> cardModel = StreamSupport.stream(cardsViews.spliterator(), false)
                .map(this::toModel)
                .collect(Collectors.toList());
        return CollectionModel.of(cardModel,
                linkTo(methodOn(CardController.class)
                        .findAll(null, null, null, null, null, null, null, null, null, null, null, null, null, null))
                        .withSelfRel());
    }
}
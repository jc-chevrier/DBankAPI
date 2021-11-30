package fr.ul.miage.chevrier.banque.assembler;

import fr.ul.miage.chevrier.banque.controller.AccountController;
import fr.ul.miage.chevrier.banque.dto.AccountView;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class AccountAssembler implements RepresentationModelAssembler<AccountView, EntityModel<AccountView>> {
    @Override
    public EntityModel<AccountView> toModel(AccountView accountView) {
        return EntityModel.of(accountView,
                              linkTo(methodOn(AccountController.class).find(accountView.getId().toString())).withSelfRel(),
                              linkTo(methodOn(AccountController.class).findAll()).withRel("collection"));
    }

    public CollectionModel<EntityModel<AccountView>> toCollectionModel(Iterable<? extends AccountView> accountsViews) {
        List<EntityModel<AccountView>> accountModel = StreamSupport.stream(accountsViews.spliterator(), false)
                                                                    .map(this::toModel)
                                                                    .collect(Collectors.toList());
        return CollectionModel.of(accountModel, linkTo(methodOn(AccountController.class).findAll()).withSelfRel());
    }
}
package fr.ul.miage.chevrier.dbank_api.mapper;

import fr.ul.miage.chevrier.dbank_api.dto.OperationInput;
import fr.ul.miage.chevrier.dbank_api.dto.OperationView;
import fr.ul.miage.chevrier.dbank_api.entity.Operation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.ArrayList;
import java.util.List;

/**
 * Mapper entité <-> vue, saisies (DTO)
 * pour les opérations sur les comptes
 * bancaires des clients.
 */
@Mapper(componentModel = "spring")
public interface OperationMapper {
    @Mapping(source = "firstAccount.id", target = "firstAccountId")
    @Mapping(source = "firstAccountCard.id", target = "firstAccountCardId")
    OperationView toView(Operation operation);

    default List<OperationView> toView(Iterable<Operation> operations) {
        var operationsViews = new ArrayList<OperationView>();
        operations.forEach(operation -> operationsViews.add(toView(operation)));
        return operationsViews;
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "rate", ignore = true)
    @Mapping(target = "dateAdded", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "firstAccount", ignore = true)
    @Mapping(target = "firstAccountCard", ignore = true)
    Operation toEntity(OperationInput OperationInput);
}
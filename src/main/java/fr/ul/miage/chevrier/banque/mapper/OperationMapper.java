package fr.ul.miage.chevrier.banque.mapper;

import fr.ul.miage.chevrier.banque.dto.OperationInput;
import fr.ul.miage.chevrier.banque.dto.OperationView;
import fr.ul.miage.chevrier.banque.entity.Operation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.ArrayList;
import java.util.List;

/**
 * Mapper entité <-> DTO (vue, saisies)
 * pour les opérations sur les comptes
 * bancaires des clients.
 */
@Mapper(componentModel = "spring")
public interface OperationMapper {
    @Mapping(source = "internalAccount.id", target = "internalAccountId")
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
    @Mapping(target = "internalAccount", ignore = true)
    Operation toEntity(OperationInput OperationInput);
}
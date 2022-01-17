package fr.ul.miage.chevrier.dbank_api.mapper;

import fr.ul.miage.chevrier.dbank_api.dto.*;
import fr.ul.miage.chevrier.dbank_api.entity.Operation;
import fr.ul.miage.chevrier.dbank_api.security.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.ArrayList;
import java.util.List;

/**
 * Mapper entité <-> vue, saisies (DTO)
 * pour les opérations sur les comptes
 * bancaires.
 */
@Mapper(componentModel = "spring")
public interface OperationMapper {
    /**
     * Transformer une entité d'une opération bancaire
     * en une vue d'opération, en fonction d'un rôle.
     *
     * @param operation           Entité d'une opération bancaire.
     * @param role                Rôle.
     * @return OperationView      Vue sur l'opération bancaire.
     */
    default OperationView toView(Operation operation, Role role) {
        if(role == Role.ADMIN || role == Role.CLIENT) {
            return new OperationCompleteView(operation.getId(), operation.getLabel(), operation.getAmount(),
                    operation.getSecondAccountName(), operation.getSecondAccountCountry(), operation.getSecondAccountIBAN(),
                    operation.getRate(), operation.getCategory(), operation.getConfirmed(), operation.getDateAdded(),
                    operation.getFirstAccount().getId(),
                    operation.getFirstAccountCard() != null ? operation.getFirstAccountCard().getId() : null);
        } else {
            return new OperationView(operation.getId(), operation.getLabel(), operation.getAmount(),
                    operation.getSecondAccountName(), operation.getSecondAccountCountry(), operation.getSecondAccountIBAN(),
                    operation.getRate(), operation.getConfirmed(), operation.getDateAdded(), operation.getFirstAccount().getId(),
                    operation.getFirstAccountCard() != null ? operation.getFirstAccountCard().getId() : null);
        }
    }

    /**
     * Transformer des entités d'opérations bancaires
     * en des vues d'opérations, en fonction d'un rôle.
     *
     * @param operations                  Entités d'opérations bancaires.
     * @param role                        Rôle.
     * @return List<OperationView>        Vues sur les entités des opérations bancaires.
     */
    default List<OperationView> toView(Iterable<Operation> operations, Role role) {
        var operationsViews = new ArrayList<OperationView>();
        operations.forEach(operation -> operationsViews.add(toView(operation, role)));
        return operationsViews;
    }

    /**
     * Transformer des saisies d'une opération bancaire
     * en une entité de l'opération.
     *
     * @param operationInput              Saisies d'une opération bancaire.
     * @return Operation                  Entité d'une opération bancaire.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "rate", ignore = true)
    @Mapping(target = "dateAdded", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "firstAccount", ignore = true)
    @Mapping(target = "firstAccountCard", ignore = true)
    Operation toEntity(OperationInput operationInput);
}
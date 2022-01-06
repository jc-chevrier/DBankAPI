package fr.ul.miage.chevrier.banque.controller;

import fr.ul.miage.chevrier.banque.assembler.OperationAssembler;
import fr.ul.miage.chevrier.banque.dto.OperationInput;
import fr.ul.miage.chevrier.banque.dto.OperationView;
import fr.ul.miage.chevrier.banque.exception.AccountNotFoundException;
import fr.ul.miage.chevrier.banque.exception.OperationNotFoundException;
import fr.ul.miage.chevrier.banque.mapper.OperationMapper;
import fr.ul.miage.chevrier.banque.repository.AccountRepository;
import fr.ul.miage.chevrier.banque.repository.OperationRepository;
import fr.ul.miage.chevrier.banque.validator.OperationValidator;
import lombok.AllArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.UUID;

/**
 * Contrôleur pour la gestion des opérations
 * sur les comptes bancaires des clients de
 * la banque.
 */
@RestController
@RequestMapping(value = "operations", produces = MediaType.APPLICATION_JSON_VALUE)
@AllArgsConstructor
public class OperationController {
    //Répertoire pour l'interrogation des comptes bancaires
    //en base de données.
    private final AccountRepository accountRepository;
    //Répertoire pour l'interrogation des
    //opérations bancaires en base de données.
    private final OperationRepository operationRepository;
    ///Mapper entité <-> DTO (vue ou saisies)
    //pour les opérations bancaires.
    private final OperationMapper operationMapper;
    //Assembleur pour associer aux vues (DTOs) des
    //opérations bancaires des liens d'actions sur
    //l'API (HATEOAS).
    private final OperationAssembler operationAssembler;
    //Validateur pour assurer la cohérence et
    //l'intégrité des comptes bancaires gérés.
    private final OperationValidator operationValidator;

    /**
     * Obtenir toutes les opérations bancaires.
     *
     * @param interval                                          Intervalle de pagination.
     * @param offset                                            Indice de début de pagination.
     * @return CollectionModel<EntityModel<OperationView>>      Collection d'opération bancaire.
     */
    @GetMapping
    public CollectionModel<EntityModel<OperationView>> findAll(
            @RequestParam(required = false, name = "interval", defaultValue = "20") Integer interval,
            @RequestParam(required = false, name = "offset", defaultValue = "0") Integer offset) {
        //Recherche des entités.
        var operations =  operationRepository.findAll(interval, offset);

        //Transformation de l'entité en vue puis ajout des liens d'actions.
        return operationAssembler.toCollectionModel(operationMapper.toView(operations));
    }

    /**
     * Obtenir une opération bancaire.
     *
     * @param operationId                     Identifiant de l'opération bancaire cherchée.
     * @return EntityModel<OperationView>     Vue sur l'opération bancaire.
     */
    @GetMapping(value = "{operationId}")
    public EntityModel<OperationView> find(@PathVariable("operationId") UUID operationId) {
        //Recherche de l'entité et levée d'une exception si l'entité n'est pas trouvée.
        var operation =  operationRepository.find(operationId).orElseThrow(() -> OperationNotFoundException.of(operationId));

        //Transformation de l'entité en vue puis ajout des liens d'actions.
        return operationAssembler.toModel(operationMapper.toView(operation));
    }

    /**
     * Créer une nouvelle opération bancaire.
     *
     * @param operationInput                  Informations saisies de l'opération bancaire à créer.
     * @return EntityModel<OperationView>     Vue sur l'opération bancaire créée.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Transactional
    public EntityModel<OperationView> create(@RequestBody @Valid OperationInput operationInput) {
        ///Création de la nouvelle entité.
        var operation = operationMapper.toEntity(operationInput);

        //Recherche de l'entité compte associée et levée d'une exception si l'entité n'est pas trouvée.
        var account = accountRepository.find(operationInput.getInternalAccountId())
                                                .orElseThrow(() -> AccountNotFoundException.of(operationInput.getInternalAccountId()));

        //Modification du solde du compte.
        account.incrementBalance(operation.getAmount());

        //TODO check pays opération
        //TODO check paramètres carte

        //Génération de l'identifiant de l'entité.
        operation.setId(UUID.randomUUID());

        //Association avec le compte bancaire.
        operation.setInternalAccount(account);

        //Sauvegarde de la nouvelle entité.
        operation = operationRepository.save(operation);

        //Transformation de l'entité en vue puis ajout des liens d'actions.
        return operationAssembler.toModel(operationMapper.toView(operation));
    }
}
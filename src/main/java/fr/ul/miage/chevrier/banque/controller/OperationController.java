package fr.ul.miage.chevrier.banque.controller;

import fr.ul.miage.chevrier.banque.assembler.OperationAssembler;
import fr.ul.miage.chevrier.banque.dto.OperationInput;
import fr.ul.miage.chevrier.banque.dto.OperationView;
import fr.ul.miage.chevrier.banque.exception.AccountNotFoundException;
import fr.ul.miage.chevrier.banque.exception.OperationConfirmedException;
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
    ///Mapper entité <-> vue ou saisies (DTO)
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
        //Recherche des opérations.
        var operations =  operationRepository.findAll(interval, offset);

        //Transformation des entités opérations en vues puis ajout des liens d'actions.
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
        //Recherche de l'oépration et levée d'une exception si l'opération n'est pas trouvée.
        var operation =  operationRepository.find(operationId)
                                                       .orElseThrow(() -> OperationNotFoundException.of(operationId));

        //Transformation de l'entité opération en vue puis ajout des liens d'actions.
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
        ///Création de la nouvelle opération à partir des saisies.
        var operation = operationMapper.toEntity(operationInput);

        //Recherche du compte associé et levée d'une exception si le compte n'est pas trouvé.
        var internalAccount = accountRepository.find(operationInput.getInternalAccountId())
                                                        .orElseThrow(() -> AccountNotFoundException.of(operationInput.getInternalAccountId()));

        //TODO check pays opération
        //TODO check paramètres carte

        //Génération de l'identifiant de l'opération.
        operation.setId(UUID.randomUUID());

        //Association avec le compte.
        operation.setInternalAccount(internalAccount);

        //Sauvegarde de la nouvelle opération.
        operation = operationRepository.save(operation);

        //Transformation de l'entité opération en vue puis ajout des liens d'actions.
        return operationAssembler.toModel(operationMapper.toView(operation));
    }

    /**
     * Confirmer une opération bancaire.
     *
     * @param operationId                     Identifiant de l'opération bancaire à confimer.
     * @return EntityModel<OperationView>     Vue sur l'opération bancaire confirmée.
     */
    @PostMapping("/confirm/{operationId}")
    @Transactional
    public EntityModel<OperationView> confirm(@PathVariable("operationId") UUID operationId) {
        //Recherche de l'opération et levée d'une exception si l'opération n'est pas trouvée.
        var operation =  operationRepository.find(operationId)
                                                       .orElseThrow(() -> OperationNotFoundException.of(operationId));

        //Modification de l'opération.
        operation.setConfirmed(true);

        //Modification du solde du compte associé à l'opération.
        operation.getInternalAccount().incrementBalance(operation.getAmount());

        //Sauvegarde des modifications.
        operation = operationRepository.save(operation);
        accountRepository.save(operation.getInternalAccount());

        //Transformation de l'entité en vue puis ajout des liens d'actions.
        return operationAssembler.toModel(operationMapper.toView(operation));
    }

    /**
     * Mettre à jour les informations d'une opération
     * bancaire pouvant être modifiée : libellé, montant,
     * pays, nom du compte externe, IBAN du compte externe,
     * catégorie, id du compte interne.
     *
     * @param operationId                       Identifiant de l'opération bancaire à modifier.
     * @param operationInput                    Informations modifiées de l'opération bancaire.
     * @return EntityModel<OperationView>       Vue sur l'opération bancaire modifiée.
     */
    @PutMapping(value = "{operationId}")
    @Transactional
    public EntityModel<OperationView> update(@PathVariable("operationId") UUID operationId,
                                             @RequestBody @Valid OperationInput operationInput) {
        //Recherche de l'entité et levée d'une exception si l'entité n'est pas trouvée.
        var operation = operationRepository.find(operationId)
                                                      .orElseThrow(() -> OperationNotFoundException.of(operationId));

        //Vérification du droit de modification.
        //Si l'opération a été confirmée.
        if (operation.isConfirmed()) {
            //Levée d'une exception.
            throw OperationConfirmedException.of(operationId);
        } else {
            //Recherche de l'entité compte associée et levée d'une exception si l'entité n'est pas trouvée.
            var internalAccount = accountRepository.find(operationInput.getInternalAccountId())
                                                            .orElseThrow(() -> AccountNotFoundException.of(operationInput.getInternalAccountId()));

            //Récupération des données saisies.
            operation.setLabel(operationInput.getLabel());
            operation.setAmount(operationInput.getAmount());
            operation.setExternalAccountName(operationInput.getExternalAccountName());
            operation.setExternalAccountIBAN(operationInput.getExternalAccountIBAN());
            operation.setCountry(operationInput.getCountry());
            //TODO check pays opération
            operation.setCategory(operationInput.getCategory());

            //Association avec le compte bancaire.
            operation.setInternalAccount(internalAccount);

            //TODO check paramètres carte

            //Sauvegarde des modifications.
            operation = operationRepository.save(operation);

            //Transformation de l'entité en vue puis ajout des liens d'actions.
            return operationAssembler.toModel(operationMapper.toView(operation));
        }
    }

    /**
     * Mettre à jour uniquement certaines informations
     * d'une opération bancaire pouvant être modifiée :
     * libellé, et/ou montant, et/ou pays, et/ou nom du
     * compte externe, et/ou IBAN du compte externe, et/ou
     * catégorie, et/ou id du compte interne.
     *
     * @param operationId                     Identifiant de l'opération bancaire à modifier.
     * @param operationInput                  Informations modifiées de l'opération bancaire.
     * @return EntityModel<OperationView>     Vue sur l'opération bancaire modifiée.
     */
    @PatchMapping(value = "{operationId}")
    @Transactional
    public EntityModel<OperationView> updatePartial(@PathVariable("operationId") UUID operationId,
                                                  @RequestBody OperationInput operationInput) {
        //Recherche de l'opération et levée d'une exception si l'oépration n'est pas trouvée.
        var operation = operationRepository.find(operationId)
                                                      .orElseThrow(() -> OperationNotFoundException.of(operationId));

        //Vérification du droit de modification.
        //Si d'autre champ autre que la catégorie ont
        //été modifié, et que l'opération a été confirmée.
        if((operationInput.getLabel() != null
           || operationInput.getAmount() != null
           || operationInput.getExternalAccountName() != null
           || operationInput.getExternalAccountIBAN() != null
           || operationInput.getCountry() != null)
           && operation.isConfirmed()) {
                //Levée d'une exception.
                throw OperationConfirmedException.of(operationId);
        }

        //Recherche du compte associé et levée d'une exception si le compte n'est pas trouvé.
        var internalAccount = accountRepository.find(operationInput.getInternalAccountId())
                                                        .orElseThrow(() -> AccountNotFoundException.of(operationInput.getInternalAccountId()));

        //Récupération des données saisies.
        if(operationInput.getLabel() != null) {
            operation.setLabel(operationInput.getLabel());
        }
        if(operationInput.getAmount() != null) {
            operation.setAmount(operationInput.getAmount());
        }
        if(operationInput.getExternalAccountName() != null) {
            operation.setExternalAccountName(operationInput.getExternalAccountName());
        }
        if(operationInput.getExternalAccountIBAN() != null) {
            operation.setExternalAccountIBAN(operationInput.getExternalAccountIBAN());
        }
        if(operationInput.getCountry() != null) {
            operation.setCountry(operationInput.getCountry());
            //TODO check pays opération
        }
        if(operationInput.getCategory() != null) {
            operation.setCategory(operation.getCategory());
        }
        if(operationInput.getInternalAccountId() != null) {
            operation.setInternalAccount(internalAccount);
        }

        //TODO check paramètres carte

        //Vérification des données saisies.
        //TODO corriger problème : erreur 500 au lieu de 400
        operationValidator.validate(new OperationInput(operationInput.getLabel(), operationInput.getAmount(),
        operation.getExternalAccountName(), operationInput.getExternalAccountIBAN(), operation.getCountry(),
        operation.getCategory(), operationInput.getInternalAccountId()));

        //Sauvegarde des modifications.
        operation = operationRepository.save(operation);

        //Transformation de l'entité en vue puis ajout des liens d'actions.
        return operationAssembler.toModel(operationMapper.toView(operation));
    }

    /**
     * Supprimer une opération bancaire.
     *
     * @param operationId       Identifiant de l'opération bancaire à supprimer.
     */
    @DeleteMapping(value = "{operationId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    public void delete(@PathVariable("operationId") UUID operationId) {
        //Recherche de l'opération.
        operationRepository.find(operationId).ifPresent((operation) -> {
                //Vérification du droit de suppression.
                //Si l'opération a été confirmée.
                if (operation.isConfirmed()) {
                    //Levée d'une exception.
                    throw OperationConfirmedException.of(operationId);
                } else {
                    //Passage de l'opération à inactive.
                    operationRepository.delete(operationId);
                }
        });
    }
}
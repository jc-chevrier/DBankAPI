package fr.ul.miage.chevrier.banque.controller;

import fr.ul.miage.chevrier.banque.assembler.OperationAssembler;
import fr.ul.miage.chevrier.banque.dto.OperationInput;
import fr.ul.miage.chevrier.banque.dto.OperationView;
import fr.ul.miage.chevrier.banque.entity.Card;
import fr.ul.miage.chevrier.banque.exception.*;
import fr.ul.miage.chevrier.banque.mapper.OperationMapper;
import fr.ul.miage.chevrier.banque.repository.AccountRepository;
import fr.ul.miage.chevrier.banque.repository.CardRepository;
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
    //Répertoire pour l'interrogation des cartes sur les
    //comptes bancaires en base de données.
    private final CardRepository cardRepository;
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
        //Recherche des opérations à partir des informations saisies.
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
        //Type d'opération : via carte ou non ?
        boolean operationIsACardPayment = operationInput.getFirstAccountCardId() != null;

        ///Création de la nouvelle opération à partir des saisies.
        var operation = operationMapper.toEntity(operationInput);

        //Recherche du premier compte associé et levée d'une exception si le compte n'est pas trouvé.
        var firstAccount = accountRepository.find(operationInput.getFirstAccountId())
                                                     .orElseThrow(() -> AccountNotFoundException.of(operationInput.getFirstAccountId()));

        //Si l'opération est un paiement par carte.
        //Recherche de la carte associée et levée d'une exception si la carte n'est pas trouvée.
        Card firstAccountCard = null;
        if(operationIsACardPayment) {
            firstAccountCard = cardRepository.find(operationInput.getFirstAccountCardId())
                                             .orElseThrow(() -> CardNotFoundException.of(operationInput.getFirstAccountCardId()));
        }

        //Génération de l'identifiant de l'opération.
        operation.setId(UUID.randomUUID());

        //Association avec le compte.
        operation.setFirstAccount(firstAccount);

        //Association avec la carte.
        //Si l'opération est un paiement par carte.
        if(operationIsACardPayment) {
            operation.setFirstAccountCard(firstAccountCard);
        }

        //TODO check pays opération
        //TODO check paramètres carte

        //Sauvegarde de la nouvelle opération.
        operation = operationRepository.save(operation);

        //Transformation de l'entité opération en vue puis ajout des liens d'actions.
        return operationAssembler.toModel(operationMapper.toView(operation));
    }

    /**
     * Confirmer une opération bancaire.
     *
     * @param operationId                     Identifiant de l'opération bancaire à confirmer.
     * @return EntityModel<OperationView>     Vue sur l'opération bancaire confirmée.
     */
    @PostMapping("/{operationId}/confirm")
    @Transactional
    public void confirm(@PathVariable("operationId") UUID operationId) {
        //Recherche de l'opération et levée d'une exception si l'opération n'est pas trouvée.
        var operation =  operationRepository.find(operationId)
                                                       .orElseThrow(() -> OperationNotFoundException.of(operationId));

        //Modification de l'opération.
        operation.setConfirmed(true);

        //Modification du solde du compte associé à l'opération.
        operation.getFirstAccount().incrementBalance(operation.getAmount());

        //Sauvegarde des modifications.
        operation = operationRepository.save(operation);
        accountRepository.save(operation.getFirstAccount());
    }

    /**
     * Mettre à jour les informations d'une opération
     * bancaire pouvant être modifiée.
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
        if (operation.getConfirmed()) {
            //Levée d'une exception.
            throw new OperationConfirmedException(operationId);
        } else {
            //Type d'opération : via carte ou non ?
            boolean operationIsACardPayment = operationInput.getFirstAccountCardId() != null;

            //Recherche de l'entité compte associée et levée d'une exception si l'entité n'est pas trouvée.
            var firstAccount = accountRepository.find(operationInput.getFirstAccountId())
                                                         .orElseThrow(() -> AccountNotFoundException.of(operationInput.getFirstAccountId()));

            //Si l'opération est un paiement par carte.
            //Recherche de la carte associée et levée d'une exception si la carte n'est pas trouvée.
            Card firstAccountCard = null;
            if(operationIsACardPayment) {
                firstAccountCard = cardRepository.find(operationInput.getFirstAccountCardId())
                                                 .orElseThrow(() -> CardNotFoundException.of(operationInput.getFirstAccountCardId()));
            }

            //Récupération des informations saisies.
            operation.setLabel(operationInput.getLabel());
            operation.setAmount(operationInput.getAmount());
            operation.setSecondAccountName(operationInput.getSecondAccountName());
            operation.setSecondAccountCountry(operationInput.getSecondAccountCountry());
            operation.setSecondAccountIBAN(operationInput.getSecondAccountIBAN());
            operation.setCategory(operationInput.getCategory());

            //Association avec le compte bancaire.
            operation.setFirstAccount(firstAccount);

            //Association avec la carte.
            //Si l'opération est un paiement par carte.
            if(operationIsACardPayment) {
                operation.setFirstAccountCard(firstAccountCard);
            } else {
                operation.setFirstAccountCard(null);
            }

            //TODO check pays opération
            //TODO check paramètres carte

            //Sauvegarde des modifications.
            operation = operationRepository.save(operation);

            //Transformation de l'entité en vue puis ajout des liens d'actions.
            return operationAssembler.toModel(operationMapper.toView(operation));
        }
    }

    /**
     * Mettre à jour uniquement certaines informations
     * d'une opération bancaire pouvant être modifiée.
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
           || operationInput.getSecondAccountName() != null
           || operationInput.getSecondAccountIBAN() != null
           || operationInput.getSecondAccountCountry() != null)
           && operation.getConfirmed()) {
                //Levée d'une exception.
                throw new OperationConfirmedException(operationId);
        }

        //Récupération des informations saisies.
        if(operationInput.getLabel() != null) {
            operation.setLabel(operationInput.getLabel());
        }
        if(operationInput.getAmount() != null) {
            operation.setAmount(operationInput.getAmount());
        }
        if(operationInput.getSecondAccountName() != null) {
            operation.setSecondAccountName(operationInput.getSecondAccountName());
        }
        if(operationInput.getSecondAccountIBAN() != null) {
            operation.setSecondAccountIBAN(operationInput.getSecondAccountIBAN());
        }
        if(operationInput.getSecondAccountCountry() != null) {
            operation.setSecondAccountCountry(operationInput.getSecondAccountCountry());
        }
        if(operationInput.getCategory() != null) {
            operation.setCategory(operation.getCategory());
        }
        if(operationInput.getFirstAccountId() != null) {
            //Recherche du compte associé et levée d'une exception si le compte n'est pas trouvé.
            var firstAccount = accountRepository.find(operationInput.getFirstAccountId())
                                                         .orElseThrow(() -> AccountNotFoundException.of(operationInput.getFirstAccountId()));
            operation.setFirstAccount(firstAccount);
        }
        if(operationInput.getFirstAccountCardId() != null) {
            //Recherche de la carte associée et levée d'une exception si la carte n'est pas trouvée.
            var firstAccountCard = cardRepository.find(operationInput.getFirstAccountCardId())
                                                        .orElseThrow(() -> CardNotFoundException.of(operationInput.getFirstAccountCardId()));
            operation.setFirstAccountCard(firstAccountCard);
        }

        //Vérification des informations saisies.
        operationValidator.validate(new OperationInput(operation.getLabel(), operation.getAmount(),
        operation.getSecondAccountName(), operation.getSecondAccountCountry(), operation.getSecondAccountIBAN(),
        operation.getCategory(), operation.getFirstAccount().getId(),
        operation.getFirstAccountCard() != null ? operation.getFirstAccountCard().getId() : null));

        //TODO check pays opération
        //TODO check paramètres carte

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
                if (operation.getConfirmed()) {
                    //Levée d'une exception.
                    throw new OperationConfirmedException(operationId);
                } else {
                    //Passage de l'opération à inactive.
                    operationRepository.delete(operationId);
                }
        });
    }
}
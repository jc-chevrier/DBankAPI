package fr.ul.miage.chevrier.dbank_api.controller;

import fr.ul.miage.chevrier.dbank_api.assembler.OperationAssembler;
import fr.ul.miage.chevrier.dbank_api.dto.OperationInput;
import fr.ul.miage.chevrier.dbank_api.dto.OperationView;
import fr.ul.miage.chevrier.dbank_api.entity.Account;
import fr.ul.miage.chevrier.dbank_api.entity.Card;
import fr.ul.miage.chevrier.dbank_api.entity.Operation;
import fr.ul.miage.chevrier.dbank_api.exception.*;
import fr.ul.miage.chevrier.dbank_api.mapper.OperationMapper;
import fr.ul.miage.chevrier.dbank_api.repository.AccountRepository;
import fr.ul.miage.chevrier.dbank_api.repository.CardRepository;
import fr.ul.miage.chevrier.dbank_api.repository.OperationRepository;
import fr.ul.miage.chevrier.dbank_api.security.Role;
import fr.ul.miage.chevrier.dbank_api.validator.OperationValidator;
import lombok.AllArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.UUID;

/**
 * Contrôleur pour la gestion des opérations
 * bancaires.
 */
@RestController
@RequestMapping(value = "operations", produces = MediaType.APPLICATION_JSON_VALUE)
@AllArgsConstructor
public class OperationController extends BaseController {
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
     * Chercher une opération bancaire et levée d'une
     * exception si l'opération n'est pas trouvée, ou
     * que les droits sont insuffisants.
     *
     * @param operationId     Identifiant de l'opération bancaire cherchée.
     * @return operation      Opération bancaire cherchée.
     */
    private Operation findOrThrowIfNotPresentOrNoAccess(UUID operationId) {
        return operationRepository.find(operationId)
                                    .filter((operation) -> {
                                        if(!operation.getFirstAccount().getSecret().contains(getFilterSecretByCurrentUserRole())) {
                                            throw new AccessDeniedException();
                                        }
                                        return true;
                                    })
                                    .orElseThrow(() -> new OperationNotFoundException(operationId));
    }

    /**
     * Chercher une carte bancaire et levée d'une
     * exception si la carte n'est pas trouvée, ou
     * que les droits sont insuffisants.
     *
     * @param cardId     Identifiant de la carte bancaire cherché.
     * @return Card      Carte bancaire cherchée.
     */
    private Card findCardOrThrowIfNotPresentOrNoAccess(UUID cardId) {
        return cardRepository.find(cardId)
                .filter((card) -> {
                    if(!card.getAccount().getSecret().contains(getFilterSecretByCurrentUserRole())) {
                        throw new AccessDeniedException();
                    }
                    return true;
                })
                .orElseThrow(() -> new CardNotFoundException(cardId));
    }

    /**
     * Chercher un compte bancaire et levée d'une
     * exception si le compte n'est pas trouvé, ou
     * que les droits sont insuffisants.
     *
     * @param accountId     Identifiant du compte bancaire cherché.
     * @return Account      Compte bancaire cherché.
     */
    private Account findAccountOrThrowIfNotPresentOrNoAccess(UUID accountId) {
        return accountRepository.find(accountId)
                .filter((account) -> {
                    if(!account.getSecret().contains(getFilterSecretByCurrentUserRole())) {
                        throw new AccessDeniedException();
                    }
                    return true;
                })
                .orElseThrow(() -> new AccountNotFoundException(accountId));
    }

    /**
     * Obtenir toutes les opérations bancaires.
     *
     * @param interval                                          Intervalle de pagination.
     * @param offset                                            Indice de début de pagination.
     * @param id                                                Identifiant de l'opération.
     * @param label                                             Libellé de l'opération.
     * @param amount                                            Montant de l'opération.
     * @param secondAccountName                                 Nom du second compte de l'opération.
     * @param secondAccountCountry                              Pays du second compte de l'opération.
     * @param secondAccountIBAN                                 IBAN du second compte de l'opération.
     * @param rate                                              Taux appliqué à l'opération.
     * @param category                                          Catégorie de l'opération.
     * @param confirmed                                         Confirmation de l'opération.
     * @param dateAdded                                         Date d'ajout de l'opération.
     * @param firstAccountId                                    Identifiant du premier compte de l'opération.
     * @param firstAccountCardId                                Identifiant de la carte du premier compte de l'opération.
     * @return CollectionModel<EntityModel<OperationView>>      Collection d'opération bancaire.
     */
    @GetMapping
    public CollectionModel<EntityModel<OperationView>> findAll(
            @RequestParam(required = false, name = "interval", defaultValue = "20") Integer interval,
            @RequestParam(required = false, name = "offset", defaultValue = "0") Integer offset,
            @RequestParam(required = false, name = "id", defaultValue = "") String id,
            @RequestParam(required = false, name = "label", defaultValue = "") String label,
            @RequestParam(required = false, name = "amount", defaultValue = "") String amount,
            @RequestParam(required = false, name = "secondAccountName", defaultValue = "") String secondAccountName,
            @RequestParam(required = false, name = "secondAccountCountry", defaultValue = "") String secondAccountCountry,
            @RequestParam(required = false, name = "secondAccountIBAN", defaultValue = "") String secondAccountIBAN,
            @RequestParam(required = false, name = "rate", defaultValue = "") String rate,
            @RequestParam(required = false, name = "category", defaultValue = "") String category,
            @RequestParam(required = false, name = "confirmed", defaultValue = "") Boolean confirmed,
            @RequestParam(required = false, name = "dateAdded", defaultValue = "") String dateAdded,
            @RequestParam(required = false, name = "firstAccountId", defaultValue = "") String firstAccountId,
            @RequestParam(required = false, name = "firstAccountCardId", defaultValue = "") String firstAccountCardId) {
        //Récupération des données de l'utilisateur connecté.
        var currentUserRole = getCurrentUserRole();
        var filterSecretByCurrentUserRole = getFilterSecretByCurrentUserRole();

        //Vérification des droits d'accès.
        if(!currentUserIs(Role.ADMIN) && (amount != null)) {
            //Pas de droit d'accès et levée d'une exception.
            throw new AccessDeniedException();
        }

        //Recherche des opérations à partir des informations saisies.
        var operations =  operationRepository.findAll(interval, offset, id.replaceAll("-", ""), label, amount,
                                                      secondAccountName, secondAccountCountry, secondAccountIBAN, rate,
                                                      category, confirmed, dateAdded, firstAccountId, firstAccountCardId,
                                                      filterSecretByCurrentUserRole);

        //Transformation des entités opérations en vues puis ajout des liens d'actions.
        return operationAssembler.toCollectionModel(operationMapper.toView(operations, currentUserRole));
    }

    /**
     * Obtenir une opération bancaire.
     *
     * @param operationId                     Identifiant de l'opération bancaire cherchée.
     * @return EntityModel<OperationView>     Vue sur l'opération bancaire.
     */
    @GetMapping(value = "{operationId}")
    public EntityModel<OperationView> find(@PathVariable("operationId") UUID operationId) {
        //Récupération des données de l'utilisateur connecté.
        var currentUserRole = getCurrentUserRole();

        //Recherche de l'oépration et levée d'une exception si l'opération n'est pas trouvée.
        var operation =  operationRepository.find(operationId)
                                                       .orElseThrow(() -> new OperationNotFoundException(operationId));

        //Transformation de l'entité opération en vue puis ajout des liens d'actions.
        return operationAssembler.toModel(operationMapper.toView(operation, currentUserRole));
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
        //Récupération des données de l'utilisateur connecté.
        var currentUserRole = getCurrentUserRole();

        //Type d'opération : via carte ou non ?
        boolean operationIsACardPayment = operationInput.getFirstAccountCardId() != null;

        ///Création de la nouvelle opération à partir des saisies.
        var operation = operationMapper.toEntity(operationInput);

        //Recherche du premier compte associé et levée d'une exception si le compte n'est pas trouvé.
        var firstAccount = findAccountOrThrowIfNotPresentOrNoAccess(operationInput.getFirstAccountId());

        //Si l'opération est un paiement par carte.
        //Recherche de la carte associée et levée d'une exception si la carte n'est pas trouvée.
        Card firstAccountCard = null;
        if(operationIsACardPayment) {
            firstAccountCard = findCardOrThrowIfNotPresentOrNoAccess(operationInput.getFirstAccountCardId());
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

        //Sauvegarde de la nouvelle opération.
        operation = operationRepository.save(operation);

        //Transformation de l'entité opération en vue puis ajout des liens d'actions.
        return operationAssembler.toModel(operationMapper.toView(operation, currentUserRole));
    }

    /**
     * Confirmer une opération bancaire.
     *
     * @param operationId                     Identifiant de l'opération bancaire à confirmer.
     * @return EntityModel<OperationView>     Vue sur l'opération bancaire confirmée.
     */
    @PostMapping("/{operationId}/confirm")
    @Transactional
    public EntityModel<OperationView> confirm(@PathVariable("operationId") UUID operationId) {
        //Récupération des données de l'utilisateur connecté.
        var currentUserRole = getCurrentUserRole();

        //Recherche de l'opération et levée d'une exception si l'opération n'est pas trouvée,
        //ou que les droits sont insuffisants.
        var operation = findOrThrowIfNotPresentOrNoAccess(operationId);

        //Modification de l'opération.
        operation.setConfirmed(true);

        //Modification du solde du compte associé à l'opération.
        operation.getFirstAccount().incrementBalance(operation.getAmount());

        //Sauvegarde des modifications.
        operationRepository.save(operation);
        accountRepository.save(operation.getFirstAccount());

        //Transformation de l'entité opération en vue puis ajout des liens d'actions.
        return operationAssembler.toModel(operationMapper.toView(operation, currentUserRole));
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
        //Récupération des données de l'utilisateur connecté.
        var currentUserRole = getCurrentUserRole();

        //Recherche de l'entité et levée d'une exception si l'entité n'est pas trouvée.
        var operation = operationRepository.find(operationId)
                                           .orElseThrow(() -> new OperationNotFoundException(operationId));

        //Vérification du droit de modification.
        //Si l'opération a été confirmée.
        if (operation.getConfirmed()) {
            //Pas de droit de modification et levée d'une exception.
            throw new OperationConfirmedException(operationId);
        } else {
            //Type d'opération : via carte ou non ?
            boolean operationIsACardPayment = operationInput.getFirstAccountCardId() != null;

            //Recherche de l'entité compte associée et levée d'une exception si l'entité n'est pas trouvée.
            var firstAccount = accountRepository.find(operationInput.getFirstAccountId())
                                                         .orElseThrow(() -> new AccountNotFoundException(operationInput.getFirstAccountId()));

            //Si l'opération est un paiement par carte.
            //Recherche de la carte associée et levée d'une exception si la carte n'est pas trouvée.
            Card firstAccountCard = null;
            if(operationIsACardPayment) {
                firstAccountCard = cardRepository.find(operationInput.getFirstAccountCardId())
                                                 .orElseThrow(() -> new CardNotFoundException(operationInput.getFirstAccountCardId()));
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

            //Sauvegarde des modifications.
            operation = operationRepository.save(operation);

            //Transformation de l'entité en vue puis ajout des liens d'actions.
            return operationAssembler.toModel(operationMapper.toView(operation, currentUserRole));
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
        //Récupération des données de l'utilisateur connecté.
        var currentUserRole = getCurrentUserRole();

        //Recherche de l'opération et levée d'une exception si l'oépration n'est pas trouvée.
        var operation = operationRepository.find(operationId)
                                            .orElseThrow(() -> new OperationNotFoundException(operationId));

        //Vérification du droit de modification.
        //Si d'autre champ autre que la catégorie ont
        //été modifié, et que l'opération a été confirmée.
        if((operationInput.getLabel() != null
           || operationInput.getAmount() != null
           || operationInput.getSecondAccountName() != null
           || operationInput.getSecondAccountIBAN() != null
           || operationInput.getSecondAccountCountry() != null)
           && operation.getConfirmed()) {
                //Pas de droit de modification et levée d'une exception.
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
                                                         .orElseThrow(() -> new AccountNotFoundException(operationInput.getFirstAccountId()));
            operation.setFirstAccount(firstAccount);
        }
        if(operationInput.getFirstAccountCardId() != null) {
            //Recherche de la carte associée et levée d'une exception si la carte n'est pas trouvée.
            var firstAccountCard = cardRepository.find(operationInput.getFirstAccountCardId())
                                                        .orElseThrow(() -> new CardNotFoundException(operationInput.getFirstAccountCardId()));
            operation.setFirstAccountCard(firstAccountCard);
        }

        //Vérification des informations saisies.
        operationValidator.validate(new OperationInput(operation.getLabel(), operation.getAmount(), operation.getSecondAccountName(),
        operation.getSecondAccountCountry(), operation.getSecondAccountIBAN(), operation.getCategory(), operation.getFirstAccount().getId(),
        operation.getFirstAccountCard() != null ? operation.getFirstAccountCard().getId() : null));

        //Sauvegarde des modifications.
        operation = operationRepository.save(operation);

        //Transformation de l'entité en vue puis ajout des liens d'actions.
        return operationAssembler.toModel(operationMapper.toView(operation, currentUserRole));
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
                    //Pas de droit de suppression et levée d'une exception.
                    throw new OperationConfirmedException(operationId);
                } else {
                    //Passage de l'opération à inactive.
                    operationRepository.delete(operationId);
                }
        });
    }
}
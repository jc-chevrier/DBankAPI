package fr.ul.miage.chevrier.dbank_api.controller;

import fr.ul.miage.chevrier.dbank_api.assembler.CardAssembler;
import fr.ul.miage.chevrier.dbank_api.dto.CardCodeInput;
import fr.ul.miage.chevrier.dbank_api.dto.CardIdentityInput;
import fr.ul.miage.chevrier.dbank_api.dto.CardInput;
import fr.ul.miage.chevrier.dbank_api.dto.CardView;
import fr.ul.miage.chevrier.dbank_api.entity.Account;
import fr.ul.miage.chevrier.dbank_api.entity.Card;
import fr.ul.miage.chevrier.dbank_api.exception.*;
import fr.ul.miage.chevrier.dbank_api.mapper.CardMapper;
import fr.ul.miage.chevrier.dbank_api.repository.AccountRepository;
import fr.ul.miage.chevrier.dbank_api.repository.CardRepository;
import fr.ul.miage.chevrier.dbank_api.security.Role;
import fr.ul.miage.chevrier.dbank_api.validator.CardValidator;
import lombok.AllArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Contrôleur pour la gestion des cartes
 * bancaires.
 */
@RestController
@RequestMapping(value = "cards", produces = MediaType.APPLICATION_JSON_VALUE)
@AllArgsConstructor
public class CardController extends BaseController {
    //Répertoire pour l'interrogation des cartes bancaires
    //en base de données.
    private final CardRepository cardRepository;
    //Répertoire pour l'interrogation des comptes bancaires
    //en base de données.
    private final AccountRepository accountRepository;
    ///Mapper entité <-> vue ou saisies (DTO) pour les
    //cartes bancaires.
    private final CardMapper cardMapper;
    //Assembleur pour associer aux vues des cartes
    //bancaires des liens d'actions sur l'API (HATEOAS).
    private final CardAssembler cardAssembler;
    //Validateur pour assurer la cohérence et l'intégrité
    //des cartes bancaires gérées.
    private final CardValidator cardValidator;

    /**
     * Chercher une carte bancaire et levée d'une
     * exception si la carte n'est pas trouvée, ou
     * que les droits sont insuffisants.
     *
     * @param cardId     Identifiant de la carte bancaire cherché.
     * @return Card      Carte bancaire cherchée.
     */
    private Card findOrThrowIfNotPresentOrNoAccess(UUID cardId) {
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
     * Obtenir toutes les cartes bancaires.
     *
     * @param interval                                      Intervalle de pagination.
     * @param offset                                        Indice de début de pagination.
     * @param id                                            Filtre partiel sur l'identifiant de la carte.
     * @param number                                        Filtre partiel sur le numéro de la carte.
     * @param cryptogram                                    Filtre partiel sur le cryptogramme de la carte.
     * @param expirationDate                                Filtre partiel sur la date d'expiration de la carte.
     * @param ceiling                                       Filtre partiel sur le plafond de la carte.
     * @param virtual                                       Filtre partiel sur la virtualité de la carte.
     * @param localization                                  Filtre partiel sur la localisation de la carte.
     * @param contactless                                   Filtre partiel sur le sans contact de la carte.
     * @param blocked                                       Filtre partiel sur le blocage de la carte.
     * @param expired                                       Filtre partiel sur l'expiration de la carte.
     * @param dateAdded                                     Filtre partiel sur la date d'ajout de la carte.
     * @param accountId                                     Filtre partiel sur l'identifiant du compte associé à la carte.
     * @return CollectionModel<EntityModel<CardView>>       Collection de cartes bancaires.
     */
    @GetMapping
    public CollectionModel<EntityModel<CardView>> findAll(
            @RequestParam(required = false, name = "interval", defaultValue = "20") Integer interval,
            @RequestParam(required = false, name = "offset", defaultValue = "0") Integer offset,
            @RequestParam(required = false, name = "id", defaultValue = "") String id,
            @RequestParam(required = false, name = "number", defaultValue = "") String number,
            @RequestParam(required = false, name = "cryptogram", defaultValue = "") String cryptogram,
            @RequestParam(required = false, name = "expirationDate", defaultValue = "") String expirationDate,
            @RequestParam(required = false, name = "ceiling", defaultValue = "") String ceiling,
            @RequestParam(required = false, name = "virtual", defaultValue = "") Boolean virtual,
            @RequestParam(required = false, name = "localization", defaultValue = "") Boolean localization,
            @RequestParam(required = false, name = "contactless", defaultValue = "") Boolean contactless,
            @RequestParam(required = false, name = "blocked", defaultValue = "") Boolean blocked,
            @RequestParam(required = false, name = "expired", defaultValue = "") Boolean expired,
            @RequestParam(required = false, name = "dateAdded", defaultValue = "") String dateAdded,
            @RequestParam(required = false, name = "accountId", defaultValue = "") String accountId) {
        //Récupération des données de l'utilisateur connecté.
        var currentUserRole = getCurrentUserRole();
        var filterSecretByCurrentUserRole = getFilterSecretByCurrentUserRole();

        //Vérification des droits d'accès.
        if(!currentUserIs(Role.ADMIN) && (!cryptogram.equals("") || !expirationDate.equals("") || !ceiling.equals(""))) {
            //Pas de droit d'accès et levée d'une exception.
            throw new AccessDeniedException();
        }

        //Recherche des cartes.
        var cards =  cardRepository.findAll(interval, offset, id.replaceAll("-", ""), number, cryptogram, expirationDate, ceiling,
                                            virtual, localization, contactless, blocked, expired, dateAdded,
                                            accountId, filterSecretByCurrentUserRole);

        //Transformation des entités cartes en vues puis ajout des liens d'actions.
        return cardAssembler.toCollectionModel(cardMapper.toView(cards, currentUserRole));
    }

    /**
     * Obtenir une carte bancaire.
     *
     * @param cardId                     Identifiant de la carte bancaire cherchée.
     * @return EntityModel<CardView>     Vue sur la carte bancaire.
     */
    @GetMapping(value = "{cardId}")
    public EntityModel<CardView> find(@PathVariable("cardId") UUID cardId) {
        //Récupération des données de l'utilisateur connecté.
        var currentUserRole = getCurrentUserRole();

        //Recherche de la carte et levée d'une exception si la carte n'est pas trouvée,
        //ou que les droits sont insuffisants.
        var card = findOrThrowIfNotPresentOrNoAccess(cardId);

        //Transformation de l'entité carte en vue puis ajout des liens d'actions.
        return cardAssembler.toModel(cardMapper.toView(card, currentUserRole));
    }

    /**
     * Créer une nouvelle carte bancaire.
     *
     * @param cardInput                  Informations saisies de la carte bancaire à créer.
     * @return EntityModel<CardView>     Vue sur la carte bancaire créée.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Transactional
    public EntityModel<CardView> create(@RequestBody @Valid CardInput cardInput) {
        //Récupération des données de l'utilisateur connecté.
        var currentUserRole = getCurrentUserRole();

        ///Création de la nouvelle carte à partir des informations saisies.
        var card = cardMapper.toEntity(cardInput);

        //Recherche du compte associé et levée d'une exception si le compte n'est pas trouvé.
        var account = findAccountOrThrowIfNotPresentOrNoAccess(cardInput.getAccountId());

        //Génération de l'identifiant de la carte.
        card.setId(UUID.randomUUID());

        //Renseignement du code haché.
        card.setCode(((Integer)card.getCode().hashCode()).toString());

        //Association avec le compte.
        card.setAccount(account);

        //Sauvegarde de la nouvelle carte.
        card = cardRepository.save(card);

        //Transformation de l'entité carte en vue puis ajout des liens d'actions.
        return cardAssembler.toModel(cardMapper.toView(card, currentUserRole));
    }

    /**
     * Vérifier les informations d'identification
     * saisies d'une carte bancaire.
     *
     * @param cardIdentityInput         Informations saisies de la carte bancaire à vérifier.
     * @return Map<String, Object>      Résultat de la vérification.
     */
    @PostMapping("/identity/check")
    public Map<String, Object> checkIdentity(@RequestBody @Valid CardIdentityInput cardIdentityInput) {
        //Vérification de l'identité du compte.
        Boolean cardIdentityChecked = cardRepository.checkIdentity(cardIdentityInput.getNumber(),
                                                                   cardIdentityInput.getCryptogram(),
                                                                   cardIdentityInput.getExpirationDate().toInstant().toString().substring(0, 7)) == 1;

        //Résultat.
        Map<String, Object> resultJSONAsMap = new HashMap<String, Object>();
        if(cardIdentityChecked) {
            resultJSONAsMap.put("checked", true);
            resultJSONAsMap.put("message", "Card identity checked.");
        } else {
            resultJSONAsMap.put("checked", false);
            resultJSONAsMap.put("message", "Card identity not checked!");
        }

        return resultJSONAsMap;
    }

    /**
     * Vérifier le code saisi d'une carte bancaire.
     *
     * @param cardId                    Identifiant de la carte bancaire à modifier.
     * @param cardCodeInput             Code saisi de la carte bancaire à vérifier.
     * @return Map<String, Object>      Résultat de la vérification.
     */
    @PostMapping("{cardId}/code/check")
    public Map<String, Object> checkCode(@PathVariable("cardId") UUID cardId,
                                         @RequestBody @Valid CardCodeInput cardCodeInput) {
        //Vérification de la carte et levée d'une exception si la carte n'est pas trouvée.
        cardRepository.find(cardId).orElseThrow(() -> new CardNotFoundException(cardId));

        //Vérification du code de la carte.
        Boolean cardIdentityChecked = cardRepository.checkCode(cardId, ((Integer)cardCodeInput.getCode().hashCode()).toString()) == 1;

        //Résultat.
        Map<String, Object> resultJSONAsMap = new HashMap<String, Object>();
        if(cardIdentityChecked) {
            resultJSONAsMap.put("checked", true);
            resultJSONAsMap.put("message", "Card code checked.");
        } else {
            resultJSONAsMap.put("checked", false);
            resultJSONAsMap.put("message", "Card code not checked!");
        }

        return resultJSONAsMap;
    }

    /**
     * Faire expirer une carte bancaire.
     *
     * @param cardId                     Identifiant de la carte bancaire à faire expirer.
     * @return EntityModel<CardView>     Vue sur la carte bancaire expirée.
     */
    @PostMapping("/{cardId}/expire")
    @Transactional
    public EntityModel<CardView> expire(@PathVariable("cardId") UUID cardId) {
        //Récupération des données de l'utilisateur connecté.
        var currentUserRole = getCurrentUserRole();

        //Recherche de la carte levée d'une exception si la carte n'est pas trouvée.
        var card = cardRepository.find(cardId).orElseThrow(() -> new CardNotFoundException(cardId));

        //Modification de la carte.
        card.setExpired(true);

        //Sauvegarde des modifications.
        cardRepository.save(card);

        //Transformation de l'entité carte en vue puis ajout des liens d'actions.
        return cardAssembler.toModel(cardMapper.toView(card, currentUserRole));
    }

    /**
     * Mettre à jour les informations d'une carte bancaire pouvant
     * être modifiée.
     *
     * @param cardId                     Identifiant de la carte bancaire à modifier.
     * @param cardInput                  Informations modifiées de la carte.
     * @return EntityModel<CardView>     Vue sur la carte bancaire modifiée.
     */
    @PutMapping(value = "{cardId}")
    @Transactional
    public EntityModel<CardView> update(@PathVariable("cardId") UUID cardId,
                                        @RequestBody @Valid CardInput cardInput) {
        //Récupération des données de l'utilisateur connecté.
        var currentUserRole = getCurrentUserRole();

        //Recherche de la carte et levée d'une exception si la carte n'est pas trouvée,
        //ou que les droits sont insuffisants.
        var card = findOrThrowIfNotPresentOrNoAccess(cardId);

        //Vérification du droit de modification.
        //Si la carte a été bloquée.
        if(card.getBlocked()) {
            //Pas de droit de modification et levée d'une exception.
            throw new CardBlockedException(cardId);
        } else {
            //Si la carte a expiré.
            if(card.getExpired()) {
                //Pas de droit de modification et levée d'une exception.
                throw new CardExpiredException(cardId);
            } else {
                //Recherche du compte associé et levée d'une exception si le compte n'est pas trouvé.
                var account = findAccountOrThrowIfNotPresentOrNoAccess(cardInput.getAccountId());

                //Récupération des informations saisies.
                card.setNumber(cardInput.getNumber());
                card.setCryptogram(cardInput.getCryptogram());
                card.setExpirationDate(cardInput.getExpirationDate());
                card.setCode(((Integer)cardInput.getCode().hashCode()).toString());
                card.setCeiling(cardInput.getCeiling());
                card.setVirtual(cardInput.getVirtual());
                card.setLocalization(cardInput.getLocalization());
                card.setContactless(cardInput.getContactless());
                card.setBlocked(cardInput.getBlocked());

                //Association avec le compte.
                card.setAccount(account);

                //Sauvegarde des modifications.
                card = cardRepository.save(card);

                //Transformation de l'entité carte en vue puis ajout des liens d'actions.
                return cardAssembler.toModel(cardMapper.toView(card, currentUserRole));
            }
        }
    }

    /**
     * Mettre à jour uniquement certaines informations
     * d'une carte bancaire pouvant être modifiée.
     *
     * @param cardId                     Identifiant de la carte bancaire à modifier.
     * @param cardInput                  Informations modifiées de la carte bancaire.
     * @return EntityModel<CardView>     Vue sur la carte bancaire modifiée.
     */
    @PatchMapping(value = "{cardId}")
    @Transactional
    public EntityModel<CardView> updatePartial(@PathVariable("cardId") UUID cardId,
                                               @RequestBody CardInput cardInput) {
        //Récupération des données de l'utilisateur connecté.
        var currentUserRole = getCurrentUserRole();

        //Recherche de la carte et levée d'une exception si la carte n'est pas trouvée,
        //ou que les droits sont insuffisants.
        var card = findOrThrowIfNotPresentOrNoAccess(cardId);

        //Vérification du droit de modification.
        //Si la carte est bloquée.
        if(card.getBlocked()) {
            //Pas de droit de modification et levée d'une exception.
            throw new CardBlockedException(cardId);
        } else {
            //Si la carte a expiré.
            if(card.getExpired()) {
                //Pas de droit de modification et levée d'une exception.
                throw new CardExpiredException(cardId);
            } else {
                //Récupération des informations saisies.
                if (cardInput.getNumber() != null) {
                    card.setNumber(cardInput.getNumber());
                }
                if (cardInput.getCryptogram() != null) {
                    card.setCryptogram(cardInput.getCryptogram());
                }
                if (cardInput.getExpirationDate() != null) {
                    card.setExpirationDate(cardInput.getExpirationDate());
                }
                if (cardInput.getCode() != null) {
                    card.setCode(((Integer)cardInput.getCode().hashCode()).toString());
                }
                if (cardInput.getCeiling() != null) {
                    card.setCeiling(cardInput.getCeiling());
                }
                if (cardInput.getVirtual() != null) {
                    card.setVirtual(cardInput.getVirtual());
                }
                if (cardInput.getLocalization() != null) {
                    card.setLocalization(cardInput.getLocalization());
                }
                if (cardInput.getContactless() != null) {
                    card.setContactless(cardInput.getContactless());
                }
                if (cardInput.getBlocked() != null) {
                    card.setBlocked(cardInput.getBlocked());
                }
                if (cardInput.getAccountId() != null) {
                    //Recherche du compte associé et levée d'une exception si le compte n'est pas trouvé.
                    var account = findAccountOrThrowIfNotPresentOrNoAccess(cardInput.getAccountId());
                    //Association avec le compte.
                    card.setAccount(account);
                }

                //Vérification des informations saisies.
                cardValidator.validate(new CardInput(card.getNumber(), card.getCryptogram(), card.getExpirationDate(),
                card.getCode(), card.getCeiling(), card.getVirtual(), card.getLocalization(), card.getContactless(),
                card.getBlocked(), card.getAccount().getId()));

                //Sauvegarde des modifications.
                card = cardRepository.save(card);

                //Transformation de l'entité carte en vue puis ajout des liens d'actions.
                return cardAssembler.toModel(cardMapper.toView(card, currentUserRole));
            }
        }
    }
}
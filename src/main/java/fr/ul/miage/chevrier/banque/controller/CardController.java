package fr.ul.miage.chevrier.banque.controller;

import fr.ul.miage.chevrier.banque.assembler.CardAssembler;
import fr.ul.miage.chevrier.banque.dto.CardCodeInput;
import fr.ul.miage.chevrier.banque.dto.CardIdentityInput;
import fr.ul.miage.chevrier.banque.dto.CardInput;
import fr.ul.miage.chevrier.banque.dto.CardView;
import fr.ul.miage.chevrier.banque.exception.AccountNotFoundException;
import fr.ul.miage.chevrier.banque.exception.CardBlockedException;
import fr.ul.miage.chevrier.banque.exception.CardNotFoundException;
import fr.ul.miage.chevrier.banque.mapper.CardMapper;
import fr.ul.miage.chevrier.banque.repository.AccountRepository;
import fr.ul.miage.chevrier.banque.repository.CardRepository;
import fr.ul.miage.chevrier.banque.validator.CardValidator;
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
 * Contrôleur pour la gestion des cartes des
 * comptes bancaires des clients de la banque.
 */
@RestController
@RequestMapping(value = "cards", produces = MediaType.APPLICATION_JSON_VALUE)
@AllArgsConstructor
public class CardController {
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
     * Obtenir toutes les cartes bancaires.
     *
     * @param interval                                      Intervalle de pagination.
     * @param offset                                        Indice de début de pagination.
     * @return CollectionModel<EntityModel<CardView>>       Collection de cartes bancaires.
     */
    @GetMapping
    public CollectionModel<EntityModel<CardView>> findAll(
            @RequestParam(required = false, name = "interval", defaultValue = "20") Integer interval,
            @RequestParam(required = false, name = "offset", defaultValue = "0") Integer offset) {
        //Recherche des cartes.
        var cards =  cardRepository.findAll(interval, offset);

        //Transformation des entités cartes en vues puis ajout des liens d'actions.
        return cardAssembler.toCollectionModel(cardMapper.toView(cards));
    }

    /**
     * Obtenir une carte bancaire.
     *
     * @param cardId                     Identifiant de la carte bancaire cherchée.
     * @return EntityModel<CardView>     Vue sur la carte bancaire.
     */
    @GetMapping(value = "{cardId}")
    public EntityModel<CardView> find(@PathVariable("cardId") UUID cardId) {
        //Recherche de la carte et levée d'une exception si la carte n'est pas trouvée.
        var card =  cardRepository.find(cardId).orElseThrow(() -> CardNotFoundException.of(cardId));

        //Transformation de l'entité carte en vue puis ajout des liens d'actions.
        return cardAssembler.toModel(cardMapper.toView(card));
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
        ///Création de la nouvelle carte à partir des informations saisies.
        var card = cardMapper.toEntity(cardInput);

        //Recherche du compte associé et levée d'une exception si le compte n'est pas trouvé.
        var account = accountRepository.find(cardInput.getAccountId())
                                                .orElseThrow(() -> AccountNotFoundException.of(cardInput.getAccountId()));

        //Génération de l'identifiant de la carte.
        card.setId(UUID.randomUUID());

        //Association avec le compte.
        card.setAccount(account);

        //Sauvegarde de la nouvelle carte.
        card = cardRepository.save(card);

        //Transformation de l'entité carte en vue puis ajout des liens d'actions.
        return cardAssembler.toModel(cardMapper.toView(card));
    }

    /**
     * Vérifier les informations d'identification
     * saisies d'une carte bancaire.
     *
     * @param cardIdentityInput       Informations saisies de la carte bancaire à vérifier.
     * @return Object                 Vue sur la carte bancaire vérifiée ou informations d'erreur.
     */
    @PostMapping("{cardId}/identity/check")
    @Transactional
    public Object checkIdentity(@RequestBody @Valid CardIdentityInput cardIdentityInput) {
        return null;
    }

    /**
     * Vérifier le code saisi d'une carte bancaire.
     *
     * @param  cardCodeInput                Code saisi de la carte bancaire à vérifier.
     * @return EntityModel<CardView>        Vue sur la carte bancaire vérifiée ou informations d'erreur.
     */
    @PostMapping("{cardId}/code/check")
    @Transactional
    public EntityModel<CardView> checkCode(@RequestBody @Valid CardCodeInput cardCodeInput) {
       return null;
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
        //Recherche de la carte et levée d'une exception si la carte n'est pas trouvée.
        var card = cardRepository.find(cardId)
                                        .orElseThrow(() -> CardNotFoundException.of(cardId));

        //Vérification du droit de modification.
        //Si la carte est bloquée.
        if(card.getBlocked()) {
            throw new CardBlockedException(cardId);
        } else {
            //Recherche du compte associé et levée d'une exception si le compte n'est pas trouvé.
            var account = accountRepository.find(cardInput.getAccountId())
                                                    .orElseThrow(() -> AccountNotFoundException.of(cardInput.getAccountId()));

            //Récupération des informations saisies.
            card.setNumber(card.getNumber());
            card.setCryptogram(card.getCryptogram());
            card.setExpirationDate(card.getExpirationDate());
            card.setCode(card.getCode());
            card.setCeiling(card.getCeiling());
            card.setVirtual(card.getVirtual());
            card.setLocalization(card.getLocalization());
            card.setContactless(card.getContactless());
            card.setBlocked(card.getBlocked());

            //Association avec le compte.
            card.setAccount(account);

            //Sauvegarde des modifications.
            card = cardRepository.save(card);

            //Transformation de l'entité carte en vue puis ajout des liens d'actions.
            return cardAssembler.toModel(cardMapper.toView(card));
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
        //Recherche de la carte et levée d'une exception si la carte n'est pas trouvée.
        var card = cardRepository.find(cardId)
                                        .orElseThrow(() -> CardNotFoundException.of(cardId));

        //Vérification du droit de modification.
        //Si la carte est bloquée.
        if(card.getBlocked()) {
            throw new CardBlockedException(cardId);
        } else {
            //Récupération des informations saisies.
            if(cardInput.getNumber() != null) {
                card.setNumber(card.getNumber());
            }
            if(cardInput.getCryptogram() != null) {
                card.setCryptogram(card.getCryptogram());
            }
            if(cardInput.getExpirationDate() != null) {
                card.setExpirationDate(card.getExpirationDate());
            }
            if(cardInput.getCode() != null) {
                card.setCode(card.getCode());
            }
            if(cardInput.getCeiling() != null) {
                card.setCeiling(card.getCeiling());
            }
            if(cardInput.getVirtual() != null) {
                card.setVirtual(card.getVirtual());
            }
            if(cardInput.getLocalization() != null) {
                card.setLocalization(card.getLocalization());
            }
            if(cardInput.getContactless() != null) {
                card.setContactless(card.getContactless());
            }
            if(cardInput.getBlocked() != null) {
                card.setBlocked(card.getBlocked());
            }
            if(cardInput.getAccountId() != null) {
                //Recherche du compte associé et levée d'une exception si le compte n'est pas trouvé.
                var account = accountRepository.find(cardInput.getAccountId())
                        .orElseThrow(() -> AccountNotFoundException.of(cardInput.getAccountId()));
                //Association avec le compte.
                card.setAccount(account);
            }

            //Vérification des informations saisies.
            cardValidator.validate(new CardInput(card.getNumber(), card.getCryptogram(), card.getExpirationDate(), card.getCode(),
                    card.getCeiling(), card.getVirtual(), card.getLocalization(), card.getContactless(), card.getBlocked(),
                    card.getAccount().getId()));

            //Sauvegarde des modifications.
            card = cardRepository.save(card);

            //Transformation de l'entité carte en vue puis ajout des liens d'actions.
            return cardAssembler.toModel(cardMapper.toView(card));
        }
    }

    /**
     * Supprimer une carte bancaire.
     *
     * @param cardId    Identifiant de la carte bancaire à supprimer.
     */
    @DeleteMapping(value = "{cardId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    public void delete(@PathVariable("cardId") UUID cardId) {
        //Passage de la carte à inactive si elle est trouvée.
        cardRepository.delete(cardId);
    }
}
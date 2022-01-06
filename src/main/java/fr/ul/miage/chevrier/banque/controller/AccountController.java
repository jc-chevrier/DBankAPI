package fr.ul.miage.chevrier.banque.controller;

import fr.ul.miage.chevrier.banque.assembler.AccountAssembler;
import fr.ul.miage.chevrier.banque.dto.AccountInput;
import fr.ul.miage.chevrier.banque.dto.AccountView;
import fr.ul.miage.chevrier.banque.service.AccountService;
import lombok.AllArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import javax.transaction.Transactional;
import java.util.UUID;

/**
 * Contrôleur pour la gestion des comptes
 * bancaires des clients de la banque.
 */
@RestController
@RequestMapping(value = "accounts", produces = MediaType.APPLICATION_JSON_VALUE)
@AllArgsConstructor
public class AccountController {
    //Service pour la couche métier de la gestion des
    //comptes bancaires.
    private final AccountService accountService;
    //Assembleur pour associer aux vues des comptes bancaires
    //des liens d'actions sur l'API (HATEOAS).
    private final AccountAssembler accountAssembler;
    //Validateur pour assurer la cohérence et l'intégrité des
    //comptes bancaires gérés.
    //private final AccountValidator accountValidator;

    /**
     * Obtenir tous les comptes bancaires.
     *
     * @param interval                                          Intervalle de pagination.
     * @param offset                                            Indice de début de pagination.
     * @return CollectionModel<EntityModel<AccountView>>        Collection de compte bancaire.
     */
    @GetMapping
    public CollectionModel<EntityModel<AccountView>> findAll(
            @RequestParam(required = false, name = "interval", defaultValue = "20") Integer interval,
            @RequestParam(required = false, name = "offset", defaultValue = "0") Integer offset) {
        return accountAssembler.toCollectionModel(accountService.findAll(interval, offset));
    }

    /**
     * Obtenir un compte bancaire.
     *
     * @param id                            Identifiant du compte bancaire cherché.
     * @return EntityModel<AccountView>     Vue sur le compte bancaire.
     */
    @GetMapping(value = "{id}")
    public EntityModel<AccountView> find(@PathVariable("id") UUID id) {
        return accountAssembler.toModel(accountService.findById(id));
    }

    /**
     * Créer un nouveau compte bancaire.
     *
     * @param input                         Informations saisies du compte bancaire à créer.
     * @return EntityModel<AccountView>     Vue sur le compte bancaire créé.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Transactional
    public EntityModel<AccountView> create(@RequestBody AccountInput input) {
        return accountAssembler.toModel(accountService.create(input));
    }

    /**
     * Mettre à jour les informations d'un compte bancaire pouvant
     * être modifié : nom, prénom, numéro de passeport, date de
     * naissance, et IBAN.
     *
     * @param id                            Identifiant du compte bancaire à modifier.
     * @param input                         Informations modifiées du compte.
     * @return EntityModel<AccountView>     Vue sur le compte bancaire modifié.
     */
    @PutMapping(value = "{id}")
    @Transactional
    public EntityModel<AccountView> update(@PathVariable("id") UUID id, @RequestBody AccountInput input) {
        return accountAssembler.toModel(accountService.update(id, input));
    }

    /**
     * Mettre à jour uniquement certaines informations d'un compte
     * bancaire pouvant être modifié : nom, et/ou prénom, et/ou
     * numéro de passeport, et/ou date de naissance, et/ou IBAN.
     *
     * @param id                            Identifiant du compte bancaire à modifier.
     * @param input                         Informations modifiées du compte bancaire.
     * @return EntityModel<AccountView>     Vue sur le compte bancaire modifié.
     */
    @PatchMapping(value = "{id}")
    @Transactional
    public EntityModel<AccountView> updatePartial(@PathVariable("id") UUID id, @RequestBody AccountInput input) {
        return accountAssembler.toModel(accountService.updatePartial(id, input));
    }

    /**
     * Supprimer un compte bancaire.
     *
     * @param id    Identifiant du compte bancaire à supprimer.
     */
    @DeleteMapping(value = "{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    public void delete(@PathVariable("id") UUID id) {
        accountService.deleteById(id);
    }
}
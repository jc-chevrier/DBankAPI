package fr.ul.miage.chevrier.dbank_api.controller;

import fr.ul.miage.chevrier.dbank_api.assembler.AccountAssembler;
import fr.ul.miage.chevrier.dbank_api.dto.AccountInput;
import fr.ul.miage.chevrier.dbank_api.dto.AccountView;
import fr.ul.miage.chevrier.dbank_api.exception.AccessDeniedException;
import fr.ul.miage.chevrier.dbank_api.exception.AccountNotFoundException;
import fr.ul.miage.chevrier.dbank_api.mapper.AccountMapper;
import fr.ul.miage.chevrier.dbank_api.repository.AccountRepository;
import fr.ul.miage.chevrier.dbank_api.validator.AccountValidator;
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
 * Contrôleur pour la gestion des comptes
 * bancaires des clients de la banque.
 */
@RestController
@RequestMapping(value = "accounts", produces = MediaType.APPLICATION_JSON_VALUE)
@AllArgsConstructor
public class AccountController {
    //Répertoire pour l'interrogation des comptes bancaires
    //en base de données.
    private final AccountRepository accountRepository;
    ///Mapper entité <-> vue ou saisies (DTO) pour les comptes bancaires.
    private final AccountMapper accountMapper;
    //Assembleur pour associer aux vues des comptes bancaires
    //des liens d'actions sur l'API (HATEOAS).
    private final AccountAssembler accountAssembler;
    //Validateur pour assurer la cohérence et l'intégrité des
    //comptes bancaires gérés.
    private final AccountValidator accountValidator;

    /**
     * Obtenir tous les comptes bancaires.
     *
     * @param interval                                      Intervalle de pagination.
     * @param offset                                        Indice de début de pagination.
     * @param id                                            Filtre partiel sur l'identifiant du compte.
     * @param firstName                                     Filtre partiel sur le prénom du client du compte.
     * @param lastName                                      Filtre partiel sur le nom du client du compte.
     * @param birthDate                                     Filtre partiel sur la date de naissance du client du compte.
     * @param country                                       Filtre partiel sur le pays du client du compte.
     * @param passportNumber                                Filtre partiel sur le numéro de passeport du client du compte.
     * @param phoneNumber                                   Filtre partiel sur le numéro de téléphone du client du compte.
     * @param IBAN                                          Filtre partiel sur l'IBAN du client du compte.
     * @param balance                                       Filtre partiel sur le solde du client du compte.
     * @param dateAdded                                     Filtre partiel sur la date d'ajout du compte.
     * @return CollectionModel<EntityModel<AccountView>>    Collection de compte bancaire.
     */
    @GetMapping
    public CollectionModel<EntityModel<AccountView>> findAll(
            @RequestParam(required = false, name = "interval", defaultValue = "20") Integer interval,
            @RequestParam(required = false, name = "offset", defaultValue = "0") Integer offset,
            @RequestParam(required = false, name = "id", defaultValue = "") String id,
            @RequestParam(required = false, name = "firstName", defaultValue = "") String firstName,
            @RequestParam(required = false, name = "lastName", defaultValue = "") String lastName,
            @RequestParam(required = false, name = "birthDate", defaultValue = "") String birthDate,
            @RequestParam(required = false, name = "country", defaultValue = "") String country,
            @RequestParam(required = false, name = "passportNumber", defaultValue = "") String passportNumber,
            @RequestParam(required = false, name = "phoneNumber", defaultValue = "") String phoneNumber,
            @RequestParam(required = false, name = "IBAN", defaultValue = "") String IBAN,
            @RequestParam(required = false, name = "balance", defaultValue = "") Double balance,
            @RequestParam(required = false, name = "dateAdded", defaultValue = "") String dateAdded
    ) {
        //Vérification des droits d'accès.
        var isExternalUser = false;//TODO à revoir
        if(isExternalUser && (!birthDate.equals("") || !passportNumber.equals("") || !phoneNumber.equals("")
        || balance != null)) {
            //Pas de droit d'accès et levée d'une exception.
            throw new AccessDeniedException();
        }

        //Recherche des comptes.
        var accounts =  accountRepository.findAll(interval, offset, id, firstName, lastName, birthDate,
                                                               country, passportNumber, phoneNumber, IBAN, balance, dateAdded);

        //Transformation des entités comptes en vues puis ajout des liens d'actions.
        return accountAssembler.toCollectionModel(accountMapper.toView(accounts));
    }

    /**
     * Obtenir un compte bancaire.
     *
     * @param accountId                     Identifiant du compte bancaire cherché.
     * @return EntityModel<AccountView>     Vue sur le compte bancaire.
     */
    @GetMapping(value = "{accountId}")
    public EntityModel<AccountView> find(@PathVariable("accountId") UUID accountId) {
        //Recherche du compte et levée d'une exception si le compte n'est pas trouvé.
        var account =  accountRepository.find(accountId).orElseThrow(() -> AccountNotFoundException.of(accountId));

        //Transformation de l'entité compte en vue puis ajout des liens d'actions.
        return accountAssembler.toModel(accountMapper.toView(account));
    }

    /**
     * Créer un nouveau compte bancaire.
     *
     * @param accountInput                  Informations saisies du compte bancaire à créer.
     * @return EntityModel<AccountView>     Vue sur le compte bancaire créé.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Transactional
    public EntityModel<AccountView> create(@RequestBody @Valid AccountInput accountInput) {
        ///Création du nouveau compte à partir des informations saisies.
        var account = accountMapper.toEntity(accountInput);

        //Génération de l'identifiant du compte.
        account.setId(UUID.randomUUID());

        //Génération du token du compte (sécurité).
        account.setSecret("secret");//TODO à revoir pour authentification.

        //Sauvegarde du nouveau compte.
        account = accountRepository.save(account);

        //Transformation de l'entité compte en vue puis ajout des liens d'actions.
        return accountAssembler.toModel(accountMapper.toView(account));
    }

    /**
     * Mettre à jour les informations d'un compte bancaire pouvant
     * être modifié : nom, prénom, numéro de passeport, date de
     * naissance, et IBAN.
     *
     * @param accountId                     Identifiant du compte bancaire à modifier.
     * @param accountInput                  Informations modifiées du compte.
     * @return EntityModel<AccountView>     Vue sur le compte bancaire modifié.
     */
    @PutMapping(value = "{accountId}")
    @Transactional
    public EntityModel<AccountView> update(@PathVariable("accountId") UUID accountId,
                                           @RequestBody @Valid AccountInput accountInput) {
        //Recherche du compte et levée d'une exception si le compte n'est pas trouvé.
        var account = accountRepository.find(accountId)
                                                .orElseThrow(() -> AccountNotFoundException.of(accountId));

        //Récupération des informations saisies.
        account.setFirstName(accountInput.getFirstName());
        account.setLastName(accountInput.getLastName());
        account.setCountry(accountInput.getCountry());
        account.setBirthDate(accountInput.getBirthDate());
        account.setPassportNumber(accountInput.getPassportNumber());
        account.setPhoneNumber(accountInput.getPhoneNumber());
        account.setIBAN(accountInput.getIBAN());

        //Sauvegarde des modifications.
        account = accountRepository.save(account);

        //Transformation de l'entité compte en vue puis ajout des liens d'actions.
        return accountAssembler.toModel(accountMapper.toView(account));
    }

    /**
     * Mettre à jour uniquement certaines informations d'un compte
     * bancaire pouvant être modifié : nom, et/ou prénom, et/ou
     * numéro de passeport, et/ou date de naissance, et/ou IBAN.
     *
     * @param accountId                     Identifiant du compte bancaire à modifier.
     * @param accountInput                  Informations modifiées du compte bancaire.
     * @return EntityModel<AccountView>     Vue sur le compte bancaire modifié.
     */
    @PatchMapping(value = "{accountId}")
    @Transactional
    public EntityModel<AccountView> updatePartial(@PathVariable("accountId") UUID accountId,
                                                  @RequestBody AccountInput accountInput) {
        //Recherche du compte et levée d'une exception si le compte n'est pas trouvé.
        var account = accountRepository.find(accountId)
                                                .orElseThrow(() -> AccountNotFoundException.of(accountId));

        //Récupération des informations saisies.
        if(accountInput.getFirstName() != null) {
            account.setFirstName(accountInput.getFirstName());
        }
        if(accountInput.getLastName() != null) {
            account.setLastName(accountInput.getLastName());
        }
        if(accountInput.getCountry() != null) {
            account.setCountry(accountInput.getCountry());
        }
        if(accountInput.getBirthDate() != null) {
            account.setBirthDate(accountInput.getBirthDate());
        }
        if(accountInput.getPassportNumber() != null) {
            account.setPassportNumber(accountInput.getPassportNumber());
        }
        if(accountInput.getPhoneNumber() != null) {
            account.setPhoneNumber(accountInput.getPhoneNumber());
        }
        if(accountInput.getIBAN() != null) {
            account.setIBAN(accountInput.getIBAN());
        }

        //Vérification des informations saisies.
        accountValidator.validate(new AccountInput(account.getFirstName(), account.getLastName(),
        account.getBirthDate(), account.getCountry(), account.getPassportNumber(), account.getPhoneNumber(), account.getIBAN()));

        //Sauvegarde des modifications.
        account = accountRepository.save(account);

        //Transformation de l'entité compte en vue puis ajout des liens d'actions.
        return accountAssembler.toModel(accountMapper.toView(account));
    }
}
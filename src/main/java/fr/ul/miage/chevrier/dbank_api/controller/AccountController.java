package fr.ul.miage.chevrier.dbank_api.controller;

import fr.ul.miage.chevrier.dbank_api.assembler.AccountAssembler;
import fr.ul.miage.chevrier.dbank_api.dto.AccountInput;
import fr.ul.miage.chevrier.dbank_api.dto.AccountView;
import fr.ul.miage.chevrier.dbank_api.entity.Account;
import fr.ul.miage.chevrier.dbank_api.exception.AccessDeniedException;
import fr.ul.miage.chevrier.dbank_api.exception.AccountNotFoundException;
import fr.ul.miage.chevrier.dbank_api.mapper.AccountMapper;
import fr.ul.miage.chevrier.dbank_api.repository.AccountRepository;
import fr.ul.miage.chevrier.dbank_api.security.Role;
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
 * bancaires.
 */
@RestController
@RequestMapping(value = "accounts", produces = MediaType.APPLICATION_JSON_VALUE)
@AllArgsConstructor
public class AccountController extends BaseController {
    //Répertoire pour l'interrogation des comptes
    //bancaires en base de données.
    private final AccountRepository accountRepository;
    ///Mapper entité <-> vue ou saisies (DTO)
    //pour les comptes bancaires.
    private final AccountMapper accountMapper;
    //Assembleur pour associer aux vues des comptes
    //bancaires, des liens d'actions sur l'API
    //(HATEOAS).
    private final AccountAssembler accountAssembler;
    //Validateur pour assurer la cohérence et
    // l'intégrité des comptes bancaires gérés.
    private final AccountValidator accountValidator;

    /**
     * Chercher un compte bancaire et levée d'une
     * exception si le compte n'est pas trouvé, ou
     * que les droits sont insuffisants.
     *
     * @param accountId     Identifiant du compte bancaire cherché.
     * @return Account      Compte bancaire cherché.
     */
    private Account findOrThrowIfNotPresentOrNoAccess(UUID accountId) {
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
     * Obtenir tous les comptes bancaires, avec un
     * système de pagination et de filtrage en fonction
     * des attributs.
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
            @RequestParam(required = false, name = "balance", defaultValue = "") String balance,
            @RequestParam(required = false, name = "dateAdded", defaultValue = "") String dateAdded) {
        //Récupération des données de l'utilisateur connecté.
        var currentUserRole = getCurrentUserRole();
        var filterSecretByCurrentUserRole = getFilterSecretByCurrentUserRole();

        //Vérification des droits d'accès de l'utilisateur connecté.
        if(!currentUserIs(Role.ADMIN) &&
           (!birthDate.equals("") || !passportNumber.equals("") || !phoneNumber.equals("") || !balance.equals(""))) {
            //Pas de droit d'accès et levée d'une exception.
            throw new AccessDeniedException();
        }

        //Recherche des comptes.
        var accounts = accountRepository.findAll(interval, offset, id.replaceAll("-", ""), firstName, lastName,
                                                 birthDate, country, passportNumber, phoneNumber, IBAN,
                                                 filterSecretByCurrentUserRole, balance, dateAdded);

        //Transformation des entités comptes en vues puis ajout des liens d'actions.
        return accountAssembler.toCollectionModel(accountMapper.toView(accounts, currentUserRole));
    }

    /**
     * Obtenir un compte bancaire.
     *
     * @param accountId                     Identifiant du compte bancaire cherché.
     * @return EntityModel<AccountView>     Vue sur le compte bancaire.
     */
    @GetMapping(value = "{accountId}")
    public EntityModel<AccountView> find(@PathVariable("accountId") UUID accountId) {
        //Récupération des données de l'utilisateur connecté.
        var currentUserRole = getCurrentUserRole();

        //Recherche du compte et levée d'une exception si le compte n'est pas trouvé, ou que
        //les droits sont insuffisants.
        var account =  findOrThrowIfNotPresentOrNoAccess(accountId);

        //Transformation de l'entité compte en vue puis ajout des liens d'actions.
        return accountAssembler.toModel(accountMapper.toView(account, currentUserRole));
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
        //Récupération des données de l'utilisateur connecté.
        var currentUser = getCurrentUser();
        var currentUserRole = getCurrentUserRole();

        ///Création du nouveau compte à partir des informations saisies.
        var account = accountMapper.toEntity(accountInput);

        //Génération de l'identifiant du compte.
        account.setId(UUID.randomUUID());

        //Association avec l'utilisateur connecté (sécurité).
        account.setSecret(currentUser.getName());

        //Sauvegarde du nouveau compte.
        account = accountRepository.save(account);

        //Transformation de l'entité compte en vue puis ajout des liens d'actions.
        return accountAssembler.toModel(accountMapper.toView(account, currentUserRole));
    }

    /**
     * Mettre à jour les informations d'un compte bancaire pouvant
     * être modifié : nom, prénom, pays, date de naissance, numéro
     * de passeport, numéro de téléphone,et IBAN.
     *
     * @param accountId                     Identifiant du compte bancaire à modifier.
     * @param accountInput                  Saisies des informations modifiées du compte.
     * @return EntityModel<AccountView>     Vue sur le compte bancaire modifié.
     */
    @PutMapping(value = "{accountId}")
    @Transactional
    public EntityModel<AccountView> update(@PathVariable("accountId") UUID accountId,
                                           @RequestBody @Valid AccountInput accountInput) {
        //Récupération des données de l'utilisateur connecté.
        var currentUserRole = getCurrentUserRole();

        //Recherche du compte et levée d'une exception si le compte n'est pas trouvé, ou que
        //les droits sont insuffisants.
        var account =  findOrThrowIfNotPresentOrNoAccess(accountId);

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
        return accountAssembler.toModel(accountMapper.toView(account, currentUserRole));
    }

    /**
     * Mettre à jour uniquement certaines informations d'un
     * compte bancaire pouvant être modifié : nom, et/ou prénom,
     * et/ou pays, et/ou date de naissance, et/ou numéro de
     * passeport, et/ou numéro de téléphone, et/ou IBAN.
     *
     * @param accountId                     Identifiant du compte bancaire à modifier.
     * @param accountInput                  Saisies des informations modifiées du compte.
     * @return EntityModel<AccountView>     Vue sur le compte bancaire modifié.
     */
    @PatchMapping(value = "{accountId}")
    @Transactional
    public EntityModel<AccountView> updatePartial(@PathVariable("accountId") UUID accountId,
                                                  @RequestBody AccountInput accountInput) {
        //Récupération des données de l'utilisateur connecté.
        var currentUserRole = getCurrentUserRole();

        //Recherche du compte et levée d'une exception si le compte n'est pas trouvé, ou que
        //les droits sont insuffisants.
        var account =  findOrThrowIfNotPresentOrNoAccess(accountId);

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
        return accountAssembler.toModel(accountMapper.toView(account, currentUserRole));
    }
}
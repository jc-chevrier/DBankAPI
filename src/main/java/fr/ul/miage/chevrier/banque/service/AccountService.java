package fr.ul.miage.chevrier.banque.service;

import fr.ul.miage.chevrier.banque.dto.AccountInput;
import fr.ul.miage.chevrier.banque.dto.AccountView;
import fr.ul.miage.chevrier.banque.exception.AccountNotFoundException;
import fr.ul.miage.chevrier.banque.mapper.AccountMapper;
import fr.ul.miage.chevrier.banque.repository.AccountRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * Service assurant la couche métier de gestion des
 * comptes bancaires de la banque.
 */
@Service
@AllArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;

    /**
     * Chercher tous les comptes bancaires actifs.
     *
     * @return List<AccountView>    Les comptes bancaires cherchés.
     */
    public List<AccountView> findAll() {//TODO filter actif
        return accountMapper.toDto(accountRepository.findAll());
    }

    /**
     * Obtenir un compte bancaire actif.
     *
     * @param id    Identifiant du compte cherché.
     * @return      Vue sur le commte cherché.
     */
    public AccountView findById(UUID id) {//TODO filter actif
        return accountMapper.toDto(accountRepository.findById(id).orElseThrow(() -> AccountNotFoundException.of(id)));
    }

    /**
     * Créer un compte bancaire.
     *
     * @param input             Informations aisies pour le compte bancaire à créer.
     * @return AccountView      Vue sur le compte bancaire créé.
     */
    public AccountView create(AccountInput input) {
        var account = accountMapper.toEntity(input);
        account.setId(UUID.randomUUID());
        account.setSecret("");//TODO à revoir pour authentification.
        account.setBalance(0.0);
        account = accountRepository.save(account);
        return accountMapper.toDto(account);
    }

    /**
     * Modifier un compte bancaire actif.
     *
     * @param id                Identifiant du compte à modifier.
     * @param input             Informations saisies pour le compte bancaire à modifier.
     * @return AccountView      Vue sur le compte bancaire modifié.
     */
    public AccountView update(UUID id, AccountInput input) {
        var account = accountRepository.findById(id)//TODO filter actif
                                                .orElseThrow(() -> AccountNotFoundException.of(id));
        account.setFirstName(input.getFirstName());
        account.setLastName(input.getLastName());
        account.setBirthDate(input.getBirthDate());
        account.setPassportNumber(input.getPassportNumber());
        account.setIBAN(input.getIBAN());
        account = accountRepository.save(account);
        return accountMapper.toDto(account);
    }

    /**
     * Modifier partiellement un compte bancaire actif.
     *
     * @param id                Identifiant du compte à modifier.
     * @param input             Informations saisies pour le compte bancaire à modifier.
     * @return AccountView      Vue sur le compte bancaire modifié.
     */
    public AccountView updatePartial(UUID id, AccountInput input) {
        var account = accountRepository.findById(id)//TODO filter actif
                                                .orElseThrow(() -> AccountNotFoundException.of(id));
        if(input.getFirstName() != null) {
            account.setFirstName(input.getFirstName());
        }
        if(input.getLastName() != null) {
            account.setLastName(input.getLastName());
        }
        if(input.getBirthDate() != null) {
            account.setBirthDate(input.getBirthDate());
        }
        if(input.getPassportNumber() != null) {
            account.setPassportNumber(input.getPassportNumber());
        }
        if(input.getIBAN() != null) {
            account.setIBAN(input.getIBAN());
        }
        account = accountRepository.save(account);
        return accountMapper.toDto(account);
    }

    /**
     * Supprimer un compte bancaire actif,
     * c'est-à-dire le rendre inactif.
     *
     * @param id        Identifiant du compte.
     */
    public void deleteById(UUID id) {
        var account = accountRepository.findById(id).get();//TODO filter actif
        account.setActive(false);
        accountRepository.save(account);
    }
}
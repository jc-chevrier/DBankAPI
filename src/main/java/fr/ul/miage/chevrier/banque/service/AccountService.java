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
     * Chercher tous les comptes bancaires.
     *
     * @param interval                  Intervalle de pagination.
     * @param offset                    Indice de début de pagination.
     * @return List<AccountView>        Collection de compte bancaire.
     */
    public List<AccountView> findAll(Integer interval, Integer offset) {
        return accountMapper.toDto(accountRepository.findAllActiveWithPagination(interval, offset));
    }

    /**
     * Chercher un compte bancaire.
     *
     * @param id                Identifiant du compte bancaire cherché.
     * @return AccountView      Vue sur le compte bancaire.
     */
    public AccountView findById(UUID id) {
        return accountMapper.toDto(accountRepository.findActiveById(id).orElseThrow(() -> AccountNotFoundException.of(id)));
    }

    /**
     * Créer un nouveau compte bancaire.
     *
     * @param input             Informations saisies du compte bancaire à créer.
     * @return AccountView      Vue sur le compte bancaire créé.
     */
    public AccountView create(AccountInput input) {
        var account = accountMapper.toEntity(input);
        account.setId(UUID.randomUUID());
        account.setSecret("secret");//TODO à revoir pour authentification.
        account.setBalance(0.0);
        account = accountRepository.save(account);
        return accountMapper.toDto(account);
    }

    /**
     * Mettre à jour les informations d'un compte bancaire pouvant
     * être modifié : nom, prénom, numéro de passeport, date de
     * naissance, et IBAN.
     *
     * @param id               Identifiant du compte bancaire à modifier.
     * @param input            Informations modifiées du compte.
     * @return AccountView     Vue sur le compte bancaire modifié.
     */
    public AccountView update(UUID id, AccountInput input) {
        var account = accountRepository.findActiveById(id)
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
     * Mettre à jour uniquement certaines informations d'un compte
     * bancaire pouvant être modifié : nom, et/ou prénom, et/ou
     * numéro de passeport, et/ou date de naissance, et/ou IBAN.
     *
     * @param id               Identifiant du compte bancaire à modifier.
     * @param input            Informations modifiées du compte bancaire.
     * @return AccountView     Vue sur le compte bancaire modifié.
     */
    public AccountView updatePartial(UUID id, AccountInput input) {
        var account = accountRepository.findActiveById(id)
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
     * Supprimer un compte bancaire, c'est-à-dire le
     * rendre inactif.
     *
     * @param id        Identifiant du compte bancaire à supprimer.
     */
    public void deleteById(UUID id) {
       accountRepository.findActiveById(id).ifPresent(account -> {
            account.setActive(false);
            accountRepository.save(account);
       });
    }
}
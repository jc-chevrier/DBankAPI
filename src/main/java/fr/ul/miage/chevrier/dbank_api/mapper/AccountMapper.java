package fr.ul.miage.chevrier.dbank_api.mapper;

import fr.ul.miage.chevrier.dbank_api.dto.AccountInput;
import fr.ul.miage.chevrier.dbank_api.dto.AccountView;
import fr.ul.miage.chevrier.dbank_api.dto.AccountCompleteView;
import fr.ul.miage.chevrier.dbank_api.entity.Account;
import fr.ul.miage.chevrier.dbank_api.security.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.ArrayList;
import java.util.List;

/**
 * Mapper entité <-> vue, saisies (DTO)
 * pour les comptes bancaires.
 */
@Mapper(componentModel = "spring")
public interface AccountMapper {
    /**
     * Transformer une entité d'un compte bancaire
     * en une vue du compte, en fonction d'un rôle.
     *
     * @param account           Entité d'un compte bancaire.
     * @param role              Rôle.
     * @return AccountView      Vue sur le compte bancaire.
     */
    default AccountView toView(Account account, Role role) {
        if(role == Role.ADMIN) {
            return new AccountCompleteView(account.getId(), account.getFirstName(), account.getLastName(),
                    account.getBirthDate(), account.getCountry(), account.getPassportNumber(), account.getPhoneNumber(),
                    account.getIBAN(), account.getBalance(), account.getDateAdded());
        } else {
            return new AccountView(account.getId(), account.getFirstName(), account.getLastName(),
                    account.getCountry(), account.getPhoneNumber(), account.getIBAN(), account.getBalance(),
                    account.getDateAdded());
        }
    }

    /**
     * Transformer des entités de comptes bancaires
     * en des vues des comptes, en fonction d'un rôle.
     *
     * @param accounts                  Entités de comptes bancaires.
     * @param role                      Rôle.
     * @return List<AccountView>        Vues sur les entités ces comptes bancaires.
     */
    default List<AccountView> toView(Iterable<Account> accounts, Role role) {
        var accountsViews = new ArrayList<AccountView>();
        accounts.forEach(account -> accountsViews.add(toView(account, role)));
        return accountsViews;
    }

    /**
     * Transformer des saisies d'un compte bancaire
     * en une entité du compte.
     *
     * @param accountInput              Saisies d'un compte bancaire.
     * @return Account                  Entité du compte bancaire.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "secret", ignore = true)
    @Mapping(target = "balance", ignore = true)
    @Mapping(target = "dateAdded", ignore = true)
    @Mapping(target = "active", ignore = true)
    Account toEntity(AccountInput accountInput);
}
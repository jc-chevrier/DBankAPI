package fr.ul.miage.chevrier.dbank_api.mapper;

import fr.ul.miage.chevrier.dbank_api.dto.AccountInput;
import fr.ul.miage.chevrier.dbank_api.dto.AccountView;
import fr.ul.miage.chevrier.dbank_api.entity.Account;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.ArrayList;
import java.util.List;

/**
 * Mapper entité <-> vue, saisies (DTO)
 * pour les comptes bancaires des clients.
 */
@Mapper(componentModel = "spring")
public interface AccountMapper {
    AccountView toView(Account account);

    default List<AccountView> toView(Iterable<Account> accounts) {
        var accountsViews = new ArrayList<AccountView>();
        accounts.forEach(account -> accountsViews.add(toView(account)));
        return accountsViews;
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "secret", ignore = true)
    @Mapping(target = "balance", ignore = true)
    @Mapping(target = "dateAdded", ignore = true)
    @Mapping(target = "active", ignore = true)
    Account toEntity(AccountInput accountInput);
}
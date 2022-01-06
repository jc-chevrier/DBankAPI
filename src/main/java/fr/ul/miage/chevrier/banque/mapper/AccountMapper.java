package fr.ul.miage.chevrier.banque.mapper;

import fr.ul.miage.chevrier.banque.dto.AccountInput;
import fr.ul.miage.chevrier.banque.dto.AccountView;
import fr.ul.miage.chevrier.banque.entity.Account;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;
import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring")
public interface AccountMapper {
    default List<AccountView> toDTO(Page<Account> page) {
        return page
                .map(this::toDTO)
                .getContent();
    }

    AccountView toDTO(Account account);

    default List<AccountView> toDTO(Iterable<Account> accounts) {
        var accountsViews = new ArrayList<AccountView>();
        accounts.forEach(account -> accountsViews.add(toDTO(account)));//TODO à revoir.
        return accountsViews;
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "dateAdded", ignore = true)
    @Mapping(target = "secret", ignore = true)
    @Mapping(target = "balance", ignore = true)
    Account toEntity(AccountInput accountInput);
}
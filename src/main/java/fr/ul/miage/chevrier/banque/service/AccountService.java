package fr.ul.miage.chevrier.banque.service;

import fr.ul.miage.chevrier.banque.assembler.AccountAssembler;
import fr.ul.miage.chevrier.banque.dto.AccountInput;
import fr.ul.miage.chevrier.banque.dto.AccountView;
import fr.ul.miage.chevrier.banque.entity.Account;
import fr.ul.miage.chevrier.banque.mapper.AccountMapper;
import fr.ul.miage.chevrier.banque.repository.AccountRepository;
import lombok.AllArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;

    public List<AccountView> findAll() {
        return accountMapper.toDto(accountRepository.findAll());
    }

    public AccountView findById(UUID UUID) {
        return accountMapper.toDto(accountRepository.findById(UUID).get());
    }

    public AccountView create(AccountInput accountInput) {
        var account = accountMapper.toEntity(accountInput);
        account.setId(UUID.randomUUID());//TODO écrire une fonction pour éviter les collisions.
        account.setDateAdded(Instant.now());
        account.setSecret("");//TODO revoir pour authentification.
        account.setBalance(0.0);
        account = accountRepository.save(account);
        return accountMapper.toDto(account);
    }
}
package fr.ul.miage.chevrier.banque.controller;

import fr.ul.miage.chevrier.banque.assembler.AccountAssembler;
import fr.ul.miage.chevrier.banque.dto.AccountInput;
import fr.ul.miage.chevrier.banque.dto.AccountView;
import fr.ul.miage.chevrier.banque.service.AccountService;
import fr.ul.miage.chevrier.banque.validator.AccountValidator;
import lombok.AllArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping(value = "accounts", produces = MediaType.APPLICATION_JSON_VALUE)
@AllArgsConstructor
public class AccountController {
    private final AccountService accountService;
    private final AccountAssembler accountAssembler;
    //private final AccountValidator accountValidator;

    @GetMapping
    public CollectionModel<EntityModel<AccountView>> findAll() {
        return accountAssembler.toCollectionModel(accountService.findAll());
    }

    @GetMapping(value = "{id}")
    public List<AccountView> find(@PathVariable("id") String UUID) {
        return null;//TODO
    }

    @PostMapping
    public AccountView create(@RequestBody AccountInput newAccount) {
        return null;//TODO
    }

    @PutMapping(value = "{id}")//TODO Vérifier route.
    public AccountView update(@PathVariable("id") String id, @RequestBody AccountInput newAccount) {
        return null;//TODO
    }

    @PatchMapping//(value = "{id}")TODO Vérifier route.
    public AccountView updatePartial(@PathVariable("id") String id, @RequestBody AccountInput newAccount) {
        return null;//TODO
    }

    @PatchMapping(value = "{id}")//TODO Vérifier route.
    public AccountView deletePartial(@PathVariable("id") String id) {
        return null;//TODO
    }
}
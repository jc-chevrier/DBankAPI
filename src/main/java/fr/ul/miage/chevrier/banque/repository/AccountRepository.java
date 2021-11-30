package fr.ul.miage.chevrier.banque.repository;

import org.springframework.stereotype.Repository;
import org.springframework.data.repository.CrudRepository;
import fr.ul.miage.chevrier.banque.entity.Account;
import java.util.UUID;

@Repository
public interface AccountRepository extends CrudRepository<Account, UUID> {}

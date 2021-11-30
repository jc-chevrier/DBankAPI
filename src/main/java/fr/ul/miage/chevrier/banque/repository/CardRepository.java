package fr.ul.miage.chevrier.banque.repository;

import org.springframework.stereotype.Repository;
import org.springframework.data.repository.CrudRepository;
import fr.ul.miage.chevrier.banque.entity.Card;
import java.util.UUID;

@Repository
public interface CardRepository extends CrudRepository<Card, UUID> {}
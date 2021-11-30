package fr.ul.miage.chevrier.banque.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import fr.ul.miage.chevrier.banque.entity.Operation;
import java.util.UUID;

@Repository
public interface OperationRepository extends CrudRepository<Operation, UUID> {}
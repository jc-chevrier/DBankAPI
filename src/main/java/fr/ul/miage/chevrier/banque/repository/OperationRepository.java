package fr.ul.miage.chevrier.banque.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import fr.ul.miage.chevrier.banque.entity.Operation;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Répertoire pour l'interrogation de la base de
 * données concernant les opérations sur les
 * comptes bancaires.
 */
@Repository
public interface OperationRepository extends CrudRepository<Operation, UUID> {
    /**
     * Chercher toutes les opérations bancaires
     * actives.
     *
     * @return List<Operation>          Opérations bancaires actives trouvées.
     */
    @Query(value = "SELECT o " +
            "FROM Operation o " +
            "WHERE o.active = true")
    List<Operation> findAll();

    /**
     * Chercher toutes les opérations bancaires
     * actives avec un système de pagination.
     *
     *
     * @param interval              Intervalle de pagination.
     * @param offset                Indice de début de pagination.
     * @return List<Operation>      Opérations actives trouvées.
     */
    @Query(value = "SELECT * " +
                   "FROM OPERATION " +
                   "WHERE ACTIVE = TRUE " +
                   "LIMIT (:interval) " +
                   "OFFSET (:offset)",
            nativeQuery = true)
    List<Operation> findAll(@Param("interval") Integer interval, @Param("offset") Integer offset);

    /**
     * Chercher une opération bancaire active en précisant son identifiant.
     *
     * @param operationId                   Identifiant de l'opération bancaire active cherchée.
     * @return Optional<Operation>          Opération bancaire active cherchée.
     */
    @Query(value = "SELECT o " +
                    "FROM Operation o " +
                    "WHERE o.id = (:operationId) " +
                    "AND o.active = true")
    Optional<Operation> find(@Param("operationId") UUID operationId);

    /**
     * Supprimer une opération bancaire en précisant son identifiant,
     * en la passant à inactive.
     *
     * @param operationId         Identifiant de l'opération bancaire à supprimer.
     */
    @Modifying
    @Query(value = "UPDATE OPERATION " +
                   "SET ACTIVE = FALSE " +
                   "WHERE ID = (:operationId) " +
                   "AND CONFIRMED = FALSE",
            nativeQuery = true)
    void delete(@Param("operationId") UUID operationId);
}
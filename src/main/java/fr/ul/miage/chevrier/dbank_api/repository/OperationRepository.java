package fr.ul.miage.chevrier.dbank_api.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import fr.ul.miage.chevrier.dbank_api.entity.Operation;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Répertoire pour l'interrogation de la base de
 * données concernant les opérations bancaires.
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
                   "LIMIT :interval " +
                   "OFFSET :offset",
            nativeQuery = true)
    List<Operation> findAll(@Param("interval") Integer interval, @Param("offset") Integer offset);

    /**
     * Chercher toutes les opérations bancaires
     * actives avec un système de pagination,
     * et de filtrage en fonction des attributs.
     *
     * @param interval                  Intervalle de pagination.
     * @param offset                    Indice de début de pagination.
     * @param id                        Identifiant de l'opération.
     * @param label                     Libellé de l'opération.
     * @param amount                    Montant de l'opération.
     * @param secondAccountName         Nom du second compte de l'opération.
     * @param secondAccountCountry      Pays du second compte de l'opération.
     * @param secondAccountIBAN         IBAN du second compte de l'opération.
     * @param rate                      Taux appliqué à l'opération.
     * @param category                  Catégorie de l'opération.
     * @param confirmed                 Confirmation de l'opération.
     * @param dateAdded                 Date d'ajout de l'opération.
     * @param firstAccountId            Identifiant du premier compte de l'opération.
     * @param firstAccountCardId        Identifiant de la carte du premier compte de l'opération.
     * @return List<Operation>          Opérations actives trouvées.
     */
    @Query(value = "SELECT O.* " +
                    "FROM OPERATION AS O " +
                    "INNER JOIN ACCOUNT AS FA " +
                    "ON FA.ID = O.FIRST_ACCOUNT_ID " +
                    "WHERE LOWER(O.ID) LIKE LOWER(CONCAT('%', :id, '%')) " +
                    "AND LOWER(O.LABEL) LIKE LOWER(CONCAT('%', :label, '%')) " +
                    "AND O.AMOUNT LIKE CONCAT('%', :amount, '%') " +
                    "AND LOWER(O.SECOND_ACCOUNT_NAME) LIKE LOWER(CONCAT('%', :secondAccountName, '%')) " +
                    "AND LOWER(O.SECOND_ACCOUNT_COUNTRY) LIKE LOWER(CONCAT('%', :secondAccountCountry, '%')) " +
                    "AND LOWER(O.SECOND_ACCOUNT_IBAN) LIKE LOWER(CONCAT('%', :secondAccountIBAN, '%')) " +
                    "AND (O.RATE IS NULL OR O.RATE LIKE CONCAT('%', :rate, '%')) " +
                    "AND (O.CATEGORY IS NULL OR LOWER(O.CATEGORY) LIKE LOWER(CONCAT('%', :category, '%'))) " +
                    "AND O.CONFIRMED LIKE CONCAT('%', :confirmed, '%') " +
                    "AND TO_CHAR(O.DATE_ADDED, 'yyyy-MM') LIKE CONCAT('%', :dateAdded, '%') " +
                    "AND LOWER(O.FIRST_ACCOUNT_ID) LIKE LOWER(CONCAT('%', :firstAccountId, '%')) " +
                    "AND (O.FIRST_ACCOUNT_CARD_ID IS NULL " +
                    "     OR LOWER(O.FIRST_ACCOUNT_CARD_ID) LIKE LOWER(CONCAT('%', :firstAccountCardId, '%'))) " +
                    "AND LOWER(FA.SECRET) LIKE LOWER(CONCAT('%', :firstAccountSecret, '%')) " +
                    "AND O.ACTIVE = TRUE " +
                    "LIMIT :interval " +
                    "OFFSET :offset",
            nativeQuery = true)
    List<Operation> findAll(@Param("interval") Integer interval, @Param("offset") Integer offset,
                            @Param("id") String id, @Param("label") String label,
                            @Param("amount") String amount, @Param("secondAccountName") String secondAccountName,
                            @Param("secondAccountCountry") String secondAccountCountry, @Param("secondAccountIBAN") String secondAccountIBAN,
                            @Param("rate") String rate, @Param("category") String category,
                            @Param("confirmed") Boolean confirmed, @Param("dateAdded") String dateAdded,
                            @Param("firstAccountId") String firstAccountId, @Param("firstAccountCardId") String firstAccountCardId,
                            @Param("firstAccountSecret") String firstAccountSecret);

    /**
     * Chercher une opération bancaire active en précisant son identifiant.
     *
     * @param operationId                   Identifiant de l'opération bancaire active cherchée.
     * @return Optional<Operation>          Opération bancaire active cherchée.
     */
    @Query(value = "SELECT o " +
                    "FROM Operation o " +
                    "WHERE o.id = :operationId " +
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
                   "WHERE ID = :operationId " +
                   "AND CONFIRMED = FALSE",
            nativeQuery = true)
    void delete(@Param("operationId") UUID operationId);
}
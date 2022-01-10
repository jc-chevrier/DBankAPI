package fr.ul.miage.chevrier.banque.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.CrudRepository;
import fr.ul.miage.chevrier.banque.entity.Account;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Répertoire pour l'interrogation de la base de
 * données concernant les comptes bancaires.
 */
@Repository
public interface AccountRepository extends CrudRepository<Account, UUID> {
    /**
     * Chercher tous les comptes bancaires actifs.
     *
     * @return List<Account>            Comptes bancaires actifs trouvés.
     */
    @Query(value = "SELECT a " +
                   "FROM Account a " +
                   "WHERE a.active = true")
    List<Account> findAll();

    /**
     * Chercher tous les comptes bancaires actifs
     * avec un système de pagination.
     *
     *
     * @param interval              Intervalle de pagination.
     * @param offset                Indice de début de pagination.
     * @return List<Account>        Comptes bancaires actifs trouvés.
     */
    @Query(value = "SELECT * " +
                    "FROM ACCOUNT " +
                    "WHERE ACTIVE = TRUE " +
                    "LIMIT :interval " +
                    "OFFSET :offset",
            nativeQuery = true)
    List<Account> findAll(@Param("interval") Integer interval, @Param("offset") Integer offset);

    /**
     * Chercher tous les comptes bancaires actifs
     * avec un système de pagination.
     *
     *
     * @param interval              Intervalle de pagination.
     * @param offset                Indice de début de pagination.
     * @return List<Account>        Comptes bancaires actifs trouvés.
     */
    @Query(value = "SELECT * " +
                    "FROM ACCOUNT " +
                    "WHERE ID LIKE CONCAT('%', :id, '%') " +
                    "AND FIRST_NAME LIKE CONCAT('%', :firstName, '%') " +
                    "AND LAST_NAME LIKE CONCAT('%', :lastName, '%') " +
                    "AND BIRTH_DATE LIKE CONCAT('%', :birthDate, '%') " +
                    "AND COUNTRY LIKE CONCAT('%', :country, '%') " +
                    "AND PASSPORT_NUMBER LIKE CONCAT('%', :passportNumber, '%') " +
                    "AND PHONE_NUMBER LIKE CONCAT('%', :phoneNumber, '%') " +
                    "AND IBAN LIKE CONCAT('%', :IBAN, '%') " +
                    "AND BALANCE LIKE CONCAT('%', :balance, '%') " +
                    "AND ACTIVE = TRUE " +
                    "LIMIT :interval " +
                    "OFFSET :offset",
            nativeQuery = true)
    List<Account> findAll(@Param("interval") Integer interval, @Param("offset") Integer offset,
                          @Param("id") Integer id, @Param("firstName") String firstName,
                          @Param("lastName") String lastName, @Param("birthDate") String birthDate,
                          @Param("country") String country, @Param("passportNumber") String passportNumber,
                          @Param("phoneNumber") String phoneNumber, @Param("IBAN") String IBAN,
                          @Param("balance") Double balance);

    /**
     * Chercher un compte bancaire actif en précisant son identifiant.
     *
     * @param accountId                 Identifiant du compte bancaire actif cherché.
     * @return Optional<Account>        Compte bancaire actif cherché.
     */
    @Query(value = "SELECT a " +
                   "FROM Account a " +
                   "WHERE a.id = :accountId " +
                   "AND a.active = true")
    Optional<Account> find(@Param("accountId") UUID accountId);

    /**
     * Supprimer un compte bancaire en précisant son identifiant,
     * en le passant à inactif.
     *
     * @param accountId         Identifiant du compte bancaire à supprimer.
     */
    @Modifying
    @Query(value = "UPDATE ACCOUNT " +
                   "SET ACTIVE = FALSE " +
                   "WHERE ID = :accountId",
            nativeQuery = true)
    void delete(@Param("accountId") UUID accountId);
}
package fr.ul.miage.chevrier.banque.repository;

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
 * concernant les comptes bancaires.
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
                    "LIMIT (:interval) " +
                    "OFFSET (:offset)",
            nativeQuery = true)
    List<Account> findAll(@Param("interval") Integer interval, @Param("offset") Integer offset);

    /**
     * Chercher un compte bancaire actif en précisant son identifiant.
     *
     * @param id                        Identifiant du compte bancaire actif cherché.
     * @return Optional<Account>        Compte bancaire actif cherché.
     */
    @Query(value = "SELECT a " +
                   "FROM Account a " +
                   "WHERE a.id = (:id) " +
                   "AND a.active = true")
    Optional<Account> find(@Param("id") UUID id);

    /**
     * Supprimer un compte bancaire en précisant son identifiant,
     * en le passant à inactif.
     *
     * @param id                        Identifiant du compte bancaire à supprimer.
     */
    @Query(value = "UPDATE Account " +
                    "SET active = false " +
                    "WHERE id = (:id)")
    void delete(@Param("id") UUID id);
}
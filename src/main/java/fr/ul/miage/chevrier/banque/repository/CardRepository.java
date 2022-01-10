package fr.ul.miage.chevrier.banque.repository;

import fr.ul.miage.chevrier.banque.entity.Account;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.CrudRepository;
import fr.ul.miage.chevrier.banque.entity.Card;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Répertoire pour l'interrogation de la base de
 * données concernant les cartes des comptes
 * bancaires.
 */
@Repository
public interface CardRepository extends CrudRepository<Card, UUID> {
    /**
     * Chercher toutes les cartes actives.
     *
     * @return List<Card>          Cartes actives trouvées.
     */
    @Query(value = "SELECT c " +
            "FROM Card c " +
            "WHERE c.active = true")
    List<Card> findAll();

    /**
     * Chercher tous les cartes bancaires actifs
     * avec un système de pagination.
     *
     *
     * @param interval              Intervalle de pagination.
     * @param offset                Indice de début de pagination.
     * @return List<Card>           Cartes bancaires actives trouvées.
     */
    @Query(value = "SELECT * " +
                   "FROM CARD " +
                   "WHERE ACTIVE = TRUE " +
                   "LIMIT :interval " +
                   "OFFSET :offset",
            nativeQuery = true)
    List<Card> findAll(@Param("interval") Integer interval, @Param("offset") Integer offset);

    /**
     * Chercher toutes les cartes actives avec
     * un système de pagination, et de filtrage
     * en fonction des attributs.
     *
     * @param interval              Intervalle de pagination.
     * @param offset                Indice de début de pagination.
     * @param id                    Filtre partiel sur l'identifiant de la carte.
     * @param number                Filtre partiel sur le numéro de la carte.
     * @param cryptogram            Filtre partiel sur le cryptogramme de la carte.
     * @param expirationDate        Filtre partiel sur la date d'expiration de la carte.
     * @param ceiling               Filtre partiel sur le plafond de la carte.
     * @param virtual               Filtre partiel sur la virtualité de la carte.
     * @param localization          Filtre partiel sur la localisation de la carte.
     * @param contactless           Filtre partiel sur le sans contact de la carte.
     * @param blocked               Filtre partiel sur le blocage de la carte.
     * @param expired               Filtre partiel sur l'expiration de la carte.
     * @param dateAdded             Filtre partiel sur la date d'ajout de la carte.
     * @return List<Card>           Cartes actives trouvées.
     */
    @Query(value = "SELECT * " +
                   "FROM CARD " +
                   "WHERE ID LIKE CONCAT('%', :id, '%') " +
                   "AND NUMBER LIKE CONCAT('%', :number, '%') " +
                   "AND CRYPTOGRAM LIKE CONCAT('%', :cryptogram, '%') " +
                   "AND EXPIRATION_DATE LIKE CONCAT('%', :expirationDate, '%') " +
                   "AND CEILING LIKE CONCAT('%', :ceiling, '%') " +
                   "AND VIRTIUAL LIKE CONCAT('%', :virtual, '%') " +
                   "AND LOCALIZATION LIKE CONCAT('%', :localization, '%') " +
                   "AND CONTACTLESS LIKE CONCAT('%', :contactless, '%') " +
                   "AND BLOCKED LIKE CONCAT('%', :blocked, '%') " +
                   "AND EXPIRED LIKE CONCAT('%', :expired, '%') " +
                   "AND DATE_ADDED LIKE CONCAT('%', :dateAdded, '%') " +
                   "AND ACTIVE = TRUE " +
                   "LIMIT :interval " +
                   "OFFSET :offset",
            nativeQuery = true)
    List<Card> findAll(@Param("interval") Integer interval, @Param("offset") Integer offset,
                       @Param("id") String id, @Param("number") String number,
                       @Param("cryptogram") String cryptogram, @Param("expirationDate") String expirationDate,
                       @Param("ceiling") Double ceiling, @Param("virtual") Boolean virtual,
                       @Param("localization") Boolean localization, @Param("contactless") Boolean contactless,
                       @Param("blocked") Boolean blocked, @Param("expired") Boolean expired,
                       @Param("dateAdded") String dateAdded);

    /**
     * Chercher une carte active en précisant son
     * identifiant.
     *
     * @param cardId                   Identifiant de la carte active cherchée.
     * @return Optional<Card>          Carte active cherchée.
     */
    @Query(value = "SELECT c " +
            "FROM Card c " +
            "WHERE c.id = :cardId " +
            "AND c.active = true")
    Optional<Card> find(@Param("cardId") UUID cardId);

    /**
     * Supprimer une carte en précisant son
     * identifiant, en la passant à inactive.
     *
     * @param cardId         Identifiant de la carte à supprimer.
     */
    @Modifying
    @Query(value = "UPDATE CARD " +
                   "SET ACTIVE = FALSE " +
                   "WHERE ID = :cardId",
            nativeQuery = true)
    void delete(@Param("cardId") UUID cardId);
}
package fr.ul.miage.chevrier.dbank_api.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.CrudRepository;
import fr.ul.miage.chevrier.dbank_api.entity.Card;

import java.util.Date;
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
     * @param accountId             Filtre partiel sur l'identifiant du compte associé à la carte.
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
                   "AND ACCOUNT_ID LIKE CONCAT('%', :accountId, '%') " +
                   "LIMIT :interval " +
                   "OFFSET :offset",
            nativeQuery = true)
    List<Card> findAll(@Param("interval") Integer interval, @Param("offset") Integer offset,
                       @Param("id") String id, @Param("number") String number,
                       @Param("cryptogram") String cryptogram, @Param("expirationDate") String expirationDate,
                       @Param("ceiling") Double ceiling, @Param("virtual") Boolean virtual,
                       @Param("localization") Boolean localization, @Param("contactless") Boolean contactless,
                       @Param("blocked") Boolean blocked, @Param("expired") Boolean expired,
                       @Param("dateAdded") String dateAdded, @Param("accountId") String accountId);

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
     * Vérifier l'identité de la carte.
     *
     * @param id                    Identifiant de la carte.
     * @param number                Numéro de la carte.
     * @param cryptogram            Cryptogramme de la carte.
     * @param expirationDate        Date d'expiration de la carte.
     * @return Boolean              Résultat de la vérification.
     */
    @Query(value = "SELECT COUNT(c) " +
            "FROM Card c " +
            "WHERE c.id = :id " +
            "AND c.number = :number " +
            "AND c.cryptogram = :cryptogram " +
            "AND c.expirationDate = :expirationDate " +
            "AND c.active = true")
    Boolean checkIdentity(@Param("id") UUID id, @Param("number") String number,
                          @Param("cryptogram") String cryptogram, @Param("expirationDate") Date expirationDate);

    /**
     * Vérifier le code de la carte.
     *
     * @param id            Identifiant de la carte.
     * @param code          Code de la carte.
     * @return Boolean      Résultat de la vérification.
     */
    @Query(value = "SELECT COUNT(c) " +
            "FROM Card c " +
            "WHERE c.id = :id " +
            "AND c.code = :code " +
            "AND c.active = TRUE")
    Boolean checkCode(@Param("id") UUID id, @Param("code") String code);

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
package fr.ul.miage.chevrier.banque.repository;

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
     * Chercher toutes les cartes actives avec
     * un système de pagination.
     *
     * @param interval              Intervalle de pagination.
     * @param offset                Indice de début de pagination.
     * @return List<Card>           Cartes actives trouvées.
     */
    @Query(value = "SELECT * " +
                   "FROM CARD " +
                   "WHERE ACTIVE = TRUE " +
                   "LIMIT :interval " +
                   "OFFSET :offset",
            nativeQuery = true)
    List<Card> findAll(@Param("interval") Integer interval, @Param("offset") Integer offset);

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
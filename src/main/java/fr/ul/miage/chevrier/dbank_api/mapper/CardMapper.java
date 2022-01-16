package fr.ul.miage.chevrier.dbank_api.mapper;

import fr.ul.miage.chevrier.dbank_api.dto.*;
import fr.ul.miage.chevrier.dbank_api.entity.Card;
import fr.ul.miage.chevrier.dbank_api.security.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.ArrayList;
import java.util.List;

/**
 * Mapper entité <-> vue, saisies (DTO)
 * pour les cartes bancaires.
 */
@Mapper(componentModel = "spring")
public interface CardMapper {
    /**
     * Transformer une entité d'une carte bancaire
     * en une vue de la carte, en fonction d'un rôle.
     *
     * @param card              Entité de la carte bancaire.
     * @param role              Rôle.
     * @return CardView         Vue de la carte bancaire.
     */
    default CardView toView(Card card, Role role) {
        if(role == Role.ADMIN) {
            return new CardCompleteView(card.getId(), card.getNumber(), card.getCryptogram(), card.getExpirationDate(),
                                        card.getCeiling(), card.getVirtual(), card.getLocalization(), card.getContactless(),
                                        card.getBlocked(), card.getExpired(), card.getDateAdded(), card.getAccount().getId());
        } else {

            return new CardView(card.getId(), card.getPartialNumber(), card.getCeiling(), card.getVirtual(), card.getLocalization(),
                                card.getContactless(), card.getBlocked(), card.getExpired(), card.getDateAdded(), card.getAccount().getId());
        }
    }

    /**
     * Transformer des entités de carte bancaire
     * en des vues des cartes, en fonction d'un rôle.
     *
     * @param cards                 Entités des cartes bancaires.
     * @param role                  Rôle.
     * @return List<CardView>       Vues des cartes bancaires.
     */
    default List<CardView> toView(Iterable<Card> cards, Role role) {
        var cardsViews = new ArrayList<CardView>();
        cards.forEach(card -> cardsViews.add(toView(card, role)));
        return cardsViews;
    }

    /**
     * Transformer des saisies d'une carte en une
     * entité de la carte.
     *
     * @param cardInput         Saisies de la carte bancaire.
     * @return Card             Entité de la carte bancaire.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "dateAdded", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "account", ignore = true)
    Card toEntity(CardInput cardInput);
}
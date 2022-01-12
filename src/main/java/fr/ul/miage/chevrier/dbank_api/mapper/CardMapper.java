package fr.ul.miage.chevrier.dbank_api.mapper;

import fr.ul.miage.chevrier.dbank_api.dto.CardInput;
import fr.ul.miage.chevrier.dbank_api.dto.CardView;
import fr.ul.miage.chevrier.dbank_api.entity.Card;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.ArrayList;
import java.util.List;

/**
 * Mapper entité <-> vue, saisies (DTO)
 * pour les cartes des comptes bancaires.
 */
@Mapper(componentModel = "spring")
public interface CardMapper {
    /**
     * Transformer une entité d'une carte en une
     * vue de la carte.
     *
     * @param card              Entité de la carte.
     * @return CardView         Vue de la carte.
     */
    @Mapping(source = "account.id", target = "accountId")
    CardView toView(Card card);

    /**
     * Transformer des entités de carte en des vues
     * de carte.
     *
     * @param cards                 Entités des cartes.
     * @return List<CardView>       Vues des cartes.
     */
    default List<CardView> toView(Iterable<Card> cards) {
        var cardsViews = new ArrayList<CardView>();
        cards.forEach(card -> cardsViews.add(toView(card)));
        return cardsViews;
    }

    /**
     * Transformer des saisies d'une carte en une
     * entité de la carte.
     *
     * @param cardInput         Saisies de la carte.
     * @return Card             Entité de la carte.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "dateAdded", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "account", ignore = true)
    Card toEntity(CardInput cardInput);
}
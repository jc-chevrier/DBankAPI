package fr.ul.miage.chevrier.banque.mapper;

import fr.ul.miage.chevrier.banque.dto.CardInput;
import fr.ul.miage.chevrier.banque.dto.CardView;
import fr.ul.miage.chevrier.banque.entity.Card;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring")
public interface CardMapper {
    CardView toDTO(Card card);

    default List<CardView> toDTO(Iterable<Card> cards) {
        var cardsViews = new ArrayList<CardView>();
        cards.forEach(card -> cardsViews.add(toDTO(card)));
        return cardsViews;
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "dateAdded", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "account", ignore = true)
    Card toEntity(CardInput cardInput);
}
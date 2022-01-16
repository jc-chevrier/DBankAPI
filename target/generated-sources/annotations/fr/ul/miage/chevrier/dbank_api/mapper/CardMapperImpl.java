package fr.ul.miage.chevrier.dbank_api.mapper;

import fr.ul.miage.chevrier.dbank_api.dto.CardInput;
import fr.ul.miage.chevrier.dbank_api.entity.Card;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2022-01-16T21:20:21+0100",
    comments = "version: 1.4.2.Final, compiler: javac, environment: Java 17.0.1 (Oracle Corporation)"
)
@Component
public class CardMapperImpl implements CardMapper {

    @Override
    public Card toEntity(CardInput cardInput) {
        if ( cardInput == null ) {
            return null;
        }

        Card card = new Card();

        card.setNumber( cardInput.getNumber() );
        card.setCryptogram( cardInput.getCryptogram() );
        card.setExpirationDate( cardInput.getExpirationDate() );
        card.setCode( cardInput.getCode() );
        card.setCeiling( cardInput.getCeiling() );
        card.setVirtual( cardInput.getVirtual() );
        card.setLocalization( cardInput.getLocalization() );
        card.setContactless( cardInput.getContactless() );
        card.setBlocked( cardInput.getBlocked() );

        return card;
    }
}

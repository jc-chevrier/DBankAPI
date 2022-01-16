package fr.ul.miage.chevrier.dbank_api.mapper;

import fr.ul.miage.chevrier.dbank_api.dto.OperationInput;
import fr.ul.miage.chevrier.dbank_api.dto.OperationView;
import fr.ul.miage.chevrier.dbank_api.entity.Account;
import fr.ul.miage.chevrier.dbank_api.entity.Card;
import fr.ul.miage.chevrier.dbank_api.entity.Operation;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2022-01-16T21:20:22+0100",
    comments = "version: 1.4.2.Final, compiler: javac, environment: Java 17.0.1 (Oracle Corporation)"
)
@Component
public class OperationMapperImpl implements OperationMapper {

    @Override
    public OperationView toView(Operation operation) {
        if ( operation == null ) {
            return null;
        }

        OperationView operationView = new OperationView();

        operationView.setFirstAccountId( operationFirstAccountId( operation ) );
        operationView.setFirstAccountCardId( operationFirstAccountCardId( operation ) );
        operationView.setId( operation.getId() );
        operationView.setLabel( operation.getLabel() );
        operationView.setAmount( operation.getAmount() );
        operationView.setSecondAccountName( operation.getSecondAccountName() );
        operationView.setSecondAccountCountry( operation.getSecondAccountCountry() );
        operationView.setSecondAccountIBAN( operation.getSecondAccountIBAN() );
        operationView.setRate( operation.getRate() );
        operationView.setCategory( operation.getCategory() );
        operationView.setDateAdded( operation.getDateAdded() );

        return operationView;
    }

    @Override
    public Operation toEntity(OperationInput OperationInput) {
        if ( OperationInput == null ) {
            return null;
        }

        Operation operation = new Operation();

        operation.setLabel( OperationInput.getLabel() );
        operation.setAmount( OperationInput.getAmount() );
        operation.setSecondAccountName( OperationInput.getSecondAccountName() );
        operation.setSecondAccountCountry( OperationInput.getSecondAccountCountry() );
        operation.setSecondAccountIBAN( OperationInput.getSecondAccountIBAN() );
        operation.setCategory( OperationInput.getCategory() );

        return operation;
    }

    private UUID operationFirstAccountId(Operation operation) {
        if ( operation == null ) {
            return null;
        }
        Account firstAccount = operation.getFirstAccount();
        if ( firstAccount == null ) {
            return null;
        }
        UUID id = firstAccount.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private UUID operationFirstAccountCardId(Operation operation) {
        if ( operation == null ) {
            return null;
        }
        Card firstAccountCard = operation.getFirstAccountCard();
        if ( firstAccountCard == null ) {
            return null;
        }
        UUID id = firstAccountCard.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }
}

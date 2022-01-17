package fr.ul.miage.chevrier.dbank_api.mapper;

import fr.ul.miage.chevrier.dbank_api.dto.OperationInput;
import fr.ul.miage.chevrier.dbank_api.entity.Operation;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2022-01-17T15:11:28+0100",
    comments = "version: 1.4.2.Final, compiler: javac, environment: Java 17.0.1 (Oracle Corporation)"
)
@Component
public class OperationMapperImpl implements OperationMapper {

    @Override
    public Operation toEntity(OperationInput operationInput) {
        if ( operationInput == null ) {
            return null;
        }

        Operation operation = new Operation();

        operation.setLabel( operationInput.getLabel() );
        operation.setAmount( operationInput.getAmount() );
        operation.setSecondAccountName( operationInput.getSecondAccountName() );
        operation.setSecondAccountCountry( operationInput.getSecondAccountCountry() );
        operation.setSecondAccountIBAN( operationInput.getSecondAccountIBAN() );
        operation.setCategory( operationInput.getCategory() );

        return operation;
    }
}

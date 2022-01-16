package fr.ul.miage.chevrier.dbank_api.mapper;

import fr.ul.miage.chevrier.dbank_api.dto.AccountInput;
import fr.ul.miage.chevrier.dbank_api.entity.Account;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2022-01-16T21:20:22+0100",
    comments = "version: 1.4.2.Final, compiler: javac, environment: Java 17.0.1 (Oracle Corporation)"
)
@Component
public class AccountMapperImpl implements AccountMapper {

    @Override
    public Account toEntity(AccountInput accountInput) {
        if ( accountInput == null ) {
            return null;
        }

        Account account = new Account();

        account.setFirstName( accountInput.getFirstName() );
        account.setLastName( accountInput.getLastName() );
        account.setBirthDate( accountInput.getBirthDate() );
        account.setCountry( accountInput.getCountry() );
        account.setPassportNumber( accountInput.getPassportNumber() );
        account.setPhoneNumber( accountInput.getPhoneNumber() );
        account.setIBAN( accountInput.getIBAN() );

        return account;
    }
}

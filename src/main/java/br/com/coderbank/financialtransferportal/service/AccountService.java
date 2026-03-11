package br.com.coderbank.financialtransferportal.service;

import br.com.coderbank.financialtransferportal.dto.request.AccountRequestDto;
import br.com.coderbank.financialtransferportal.dto.response.AccountResponseDto;
import br.com.coderbank.financialtransferportal.entity.Account;
import br.com.coderbank.financialtransferportal.mapper.AccountMapper;
import br.com.coderbank.financialtransferportal.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;


@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository repository;
    private final AccountMapper mapper;
    private static final BigDecimal INITIAL_BALANCE = BigDecimal.ZERO;
    private static final String AGENCY_NUMBER = "0001";

    public AccountResponseDto create(AccountRequestDto accountRequestDto) {
        Account accountEntity = mapper.toEntity(accountRequestDto);

        accountEntity.setBalance(INITIAL_BALANCE);
        accountEntity.setAgencyNumber(AGENCY_NUMBER);
        accountEntity.setAccountNumber(generateAccountNumber());
        accountEntity.setClientId(accountRequestDto.clientId());

        Account savedAccount = repository.save(accountEntity);

        return mapper.toDto(savedAccount);
    }

    public Optional<AccountResponseDto> getDetails(String id){
        var accountId = UUID.fromString(id);

        return repository.findById(accountId)
                .map(mapper::toDto);
    }

    private String generateAccountNumber(){

        String accountNumber;

        do{
            int number = ThreadLocalRandom.current().nextInt(10000, 99999);

            int digit = number % 9;

            accountNumber = number + "-" + digit;
        } while (repository.existsByAccountNumber(accountNumber));

        return accountNumber;
    }
}

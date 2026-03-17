package br.com.coderbank.financialtransferportal.service;

import br.com.coderbank.financialtransferportal.dto.request.AccountRequestDto;
import br.com.coderbank.financialtransferportal.dto.response.AccountResponseDto;
import br.com.coderbank.financialtransferportal.dto.response.TransactionResponseDto;
import br.com.coderbank.financialtransferportal.entity.Account;
import br.com.coderbank.financialtransferportal.mapper.AccountMapper;
import br.com.coderbank.financialtransferportal.mapper.TransactionMapper;
import br.com.coderbank.financialtransferportal.repository.AccountRepository;
import br.com.coderbank.financialtransferportal.repository.TransactionRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository repository;
    private final TransactionRepository transactionRepository;
    private final AccountMapper mapper;
    private final TransactionMapper transactionMapper;
    private static final BigDecimal INITIAL_BALANCE = BigDecimal.ZERO;
    private static final String AGENCY_NUMBER = "0001";

    public AccountResponseDto create(AccountRequestDto accountRequestDto) {
        Account accountEntity = mapper.toEntity(accountRequestDto);

        accountEntity.setBalance(INITIAL_BALANCE);
        accountEntity.setAgencyNumber(AGENCY_NUMBER);
        accountEntity.setAccountNumber(generateAccountNumber());
        accountEntity.setClientId(accountRequestDto.customerId());

        Account savedAccount = repository.save(accountEntity);

        return mapper.toDto(savedAccount);
    }

    public AccountResponseDto getDetails(String id){
        var accountId = UUID.fromString(id);

        return repository.findById(accountId)
                .map(mapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Account not found"));
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

    public List<TransactionResponseDto> accountTransactions(String id){

        var idAccount = UUID.fromString(id);

        if (!repository.existsById(idAccount)){
            throw new EntityNotFoundException("Account not found");
        }

        return transactionRepository
                .findBySourceAccountOrDestinationAccount(idAccount, idAccount)
                .stream()
                .map(transactionMapper::toDto)
                .toList();
    }

}
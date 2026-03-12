package br.com.coderbank.financialtransferportal.service;

import br.com.coderbank.financialtransferportal.dto.request.AccountRequestDto;
import br.com.coderbank.financialtransferportal.dto.response.AccountResponseDto;
import br.com.coderbank.financialtransferportal.entity.Account;
import br.com.coderbank.financialtransferportal.exception.InsufficientBalanceException;
import br.com.coderbank.financialtransferportal.mapper.AccountMapper;
import br.com.coderbank.financialtransferportal.repository.AccountRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public void deposit(BigDecimal amount, UUID accoundId){
        Account account = repository
                .findById(accoundId)
                .orElseThrow(() -> new EntityNotFoundException("Account not found"));

        account.setBalance(account.getBalance().add(amount));
    }

    @Transactional
    public void withdraw(BigDecimal amount, UUID accoundId){
        Account account = repository
                .findById(accoundId)
                .orElseThrow(() -> new EntityNotFoundException("Account not found"));

        if (account.getBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException("Insufficient balance to execute withdraw");
        }

        account.setBalance(account.getBalance().subtract(amount));
    }

    @Transactional
    public void transfer(BigDecimal amount, UUID sourceAccountId, UUID destinationAccountId){
        Account sourceAccount = repository
                .findById(sourceAccountId)
                .orElseThrow(() -> new EntityNotFoundException("Account not found"));

        Account destinationAccount = repository
                .findById(destinationAccountId)
                .orElseThrow(() -> new EntityNotFoundException("Account not found"));

        if (sourceAccount.getBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException("Insufficient balance to execute transfer");
        }

        sourceAccount.setBalance(sourceAccount.getBalance().subtract(amount));
        destinationAccount.setBalance(destinationAccount.getBalance().add(amount));
    }
}
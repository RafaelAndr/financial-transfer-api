package br.com.coderbank.financialtransferportal.service;

import br.com.coderbank.financialtransferportal.dto.request.TransactionRequestDto;
import br.com.coderbank.financialtransferportal.dto.response.TransactionResponseDto;
import br.com.coderbank.financialtransferportal.entity.Account;
import br.com.coderbank.financialtransferportal.entity.Transaction;
import br.com.coderbank.financialtransferportal.enums.TransactionStatus;
import br.com.coderbank.financialtransferportal.enums.TransactionType;
import br.com.coderbank.financialtransferportal.exception.InsufficientBalanceException;
import br.com.coderbank.financialtransferportal.mapper.TransactionMapper;
import br.com.coderbank.financialtransferportal.repository.AccountRepository;
import br.com.coderbank.financialtransferportal.repository.TransactionRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository repository;
    private final AccountRepository accountRepository;
    private final TransactionMapper mapper;

    private TransactionResponseDto saveTransaction(Transaction transaction){

        Transaction transactionSaved = repository.save(transaction);

        return mapper.toDto(transactionSaved);
    }

    @Transactional
    public TransactionResponseDto executeTransaction(TransactionRequestDto transactionRequestDto) {
        Transaction transaction = mapper.toEntity(transactionRequestDto);

        if (transactionRequestDto.type() == TransactionType.DEPOSIT) {
            deposit(transactionRequestDto.amount(), transactionRequestDto.destinationAccount());
            transaction.setStatus(TransactionStatus.COMPLETED);
            return saveTransaction(transaction);
        }
        else if (transactionRequestDto.type() == TransactionType.WITHDRAW) {
            withdraw(transactionRequestDto.amount(), transactionRequestDto.sourceAccount());
            transaction.setStatus(TransactionStatus.COMPLETED);
            return saveTransaction(transaction);
        }
        else {
            transfer(transactionRequestDto.amount(), transactionRequestDto.sourceAccount(), transactionRequestDto.destinationAccount());
            transaction.setStatus(TransactionStatus.COMPLETED);
            return saveTransaction(transaction);
        }
    }

    @Transactional
    public void deposit(BigDecimal amount, UUID accoundId){
        Account account = accountRepository
                .findById(accoundId)
                .orElseThrow(() -> new EntityNotFoundException("Account not found"));

        account.setBalance(account.getBalance().add(amount));
    }

    @Transactional
    public void withdraw(BigDecimal amount, UUID accoundId){
        Account account = accountRepository
                .findById(accoundId)
                .orElseThrow(() -> new EntityNotFoundException("Account not found"));

        if (account.getBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException("Insufficient balance to execute withdraw");
        }

        account.setBalance(account.getBalance().subtract(amount));
    }

    @Transactional
    public void transfer(BigDecimal amount, UUID sourceAccountId, UUID destinationAccountId){
        Account sourceAccount = accountRepository
                .findById(sourceAccountId)
                .orElseThrow(() -> new EntityNotFoundException("Account not found"));

        Account destinationAccount = accountRepository
                .findById(destinationAccountId)
                .orElseThrow(() -> new EntityNotFoundException("Account not found"));

        if (sourceAccount.getBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException("Insufficient balance to execute transfer");
        }

        sourceAccount.setBalance(sourceAccount.getBalance().subtract(amount));
        destinationAccount.setBalance(destinationAccount.getBalance().add(amount));
    }
}

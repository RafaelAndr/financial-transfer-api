package br.com.coderbank.financialtransferportal.service;

import br.com.coderbank.financialtransferportal.dto.request.TransactionRequestDto;
import br.com.coderbank.financialtransferportal.dto.response.TransactionResponseDto;
import br.com.coderbank.financialtransferportal.entity.Transaction;
import br.com.coderbank.financialtransferportal.enums.TransactionStatus;
import br.com.coderbank.financialtransferportal.enums.TransactionType;
import br.com.coderbank.financialtransferportal.mapper.TransactionMapper;
import br.com.coderbank.financialtransferportal.repository.AccountRepository;
import br.com.coderbank.financialtransferportal.repository.TransactionRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository repository;
    private final AccountRepository accountRepository;
    private final AccountService accountService;
    private final TransactionMapper mapper;

    private TransactionResponseDto saveTransaction(Transaction transaction){

        Transaction transactionSaved = repository.save(transaction);

        return mapper.toDto(transactionSaved);
    }

    @Transactional
    public TransactionResponseDto executeTransaction(TransactionRequestDto transactionRequestDto) {
        Transaction transaction = mapper.toEntity(transactionRequestDto);

        if (transactionRequestDto.type() == TransactionType.DEPOSIT) {
            accountService.deposit(transactionRequestDto.amount(), transactionRequestDto.destinationAccount());
            transaction.setStatus(TransactionStatus.COMPLETED);
            return saveTransaction(transaction);
        }
        else if (transactionRequestDto.type() == TransactionType.WITHDRAW) {
            accountService.withdraw(transactionRequestDto.amount(), transactionRequestDto.sourceAccount());
            transaction.setStatus(TransactionStatus.COMPLETED);
            return saveTransaction(transaction);
        }
        else {
            accountService.transfer(transactionRequestDto.amount(), transactionRequestDto.sourceAccount(), transactionRequestDto.destinationAccount());
            transaction.setStatus(TransactionStatus.COMPLETED);
            return saveTransaction(transaction);
        }
    }

    public List<TransactionResponseDto> accountTransactions(String id){

        var idAccount = UUID.fromString(id);

        if (!accountRepository.existsById(idAccount)){
            throw new EntityNotFoundException("Account not found");
        }

        return repository
                .findBySourceAccountOrDestinationAccount(idAccount, idAccount)
                .stream()
                .map(mapper::toDto)
                .toList();
    }
}

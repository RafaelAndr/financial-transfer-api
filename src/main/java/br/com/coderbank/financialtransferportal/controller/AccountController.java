package br.com.coderbank.financialtransferportal.controller;

import br.com.coderbank.financialtransferportal.dto.request.AccountRequestDto;
import br.com.coderbank.financialtransferportal.dto.response.AccountResponseDto;
import br.com.coderbank.financialtransferportal.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService service;

    @PostMapping
    public ResponseEntity<AccountResponseDto> create(@RequestBody @Valid AccountRequestDto accountRequestDto){

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.create(accountRequestDto));
    }

    @GetMapping("{id}")
    public ResponseEntity<AccountResponseDto> getDetails(@PathVariable("id") String id){

        return service.getDetails(id)
                .map(ResponseEntity::ok)
                .orElseGet( () -> ResponseEntity.notFound().build());
    }
}

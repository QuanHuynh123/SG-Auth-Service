package com.example.Makeup.controller;

import com.example.Makeup.dto.model.AccountDTO;
import com.example.Makeup.dto.request.UpdateAccountRequest;
import com.example.Makeup.dto.response.common.ApiResponse;
import com.example.Makeup.service.IAccountService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth/api/accounts")
@RequiredArgsConstructor
public class AccountRestController {

  private final IAccountService accountService;

  /** Get all accounts */
  @GetMapping
  public ApiResponse<List<AccountDTO>> getAllAccounts() {
    return ApiResponse.success("",accountService.getAllAccounts());
  }

  /** Get account by ID */
  @GetMapping("/{id}")
  public ApiResponse<AccountDTO> getAccountById(@PathVariable("id") UUID accountId) {
    return ApiResponse.success("",accountService.getAccountById(accountId));
  }

  /** Create account */
  @PostMapping()
  public ApiResponse<AccountDTO> createAccount(@RequestBody AccountDTO accountDTO) {
    return ApiResponse.success("",accountService.createAccount(accountDTO));
  }

  /** Update account */
  @PutMapping("/{id}")
  public ResponseEntity<Boolean> updateAccount(
      @RequestBody UpdateAccountRequest updateAccountRequest, @PathVariable("id") UUID accountId) {
    return null; // accountService.updateAccount(updateAccountRequest, accountId);
  }

  /** Delete account */
  @DeleteMapping("/{id}")
  public ApiResponse<Boolean> deleteAccount(@PathVariable("id") UUID accountId) {
    return ApiResponse.success("",accountService.deleteAccount(accountId));
  }
}

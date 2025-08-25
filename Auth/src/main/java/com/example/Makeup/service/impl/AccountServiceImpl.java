package com.example.Makeup.service.impl;

import com.example.Makeup.dto.model.AccountDTO;
import com.example.Makeup.dto.request.RegisterRequest;
import com.example.Makeup.dto.request.UpdateAccountRequest;
import com.example.Makeup.dto.response.AuthResponse;
import com.example.Makeup.entity.Account;
import com.example.Makeup.entity.RefreshToken;
import com.example.Makeup.entity.Role;
import com.example.Makeup.entity.User;
import com.example.Makeup.enums.ErrorCode;
import com.example.Makeup.enums.RoleType;
import com.example.Makeup.exception.AppException;
import com.example.Makeup.mapper.AccountMapper;
import com.example.Makeup.repository.AccountRepository;
import com.example.Makeup.repository.RefreshTokenRepository;
import com.example.Makeup.repository.RoleRepository;
import com.example.Makeup.repository.UserRepository;
import com.example.Makeup.security.JWTProvider;
import com.example.Makeup.service.IAccountService;
import jakarta.transaction.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements UserDetailsService, IAccountService {

  private final AccountRepository accountRepository;
  private final RoleRepository roleRepository;
  private final AccountMapper accountMapper;
  private final JWTProvider jwtProvider;
  private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
  private final UserRepository userRepository;
  private final RefreshTokenRepository refreshTokenRepository;
  private final RedisTemplate<String, Object> redisTemplate;

  @Value("${refresh.expiration}")
  private long expiredRefreshToken;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    Optional<Account> account = accountRepository.findByUserName(username);
    if (account.isPresent()) {
      var userObj = account.get();
      return org.springframework.security.core.userdetails.User.builder()
          .username(userObj.getUserName())
          .password(userObj.getPassWord())
          .authorities(getAuthorities(account.get()))
          .build();
    } else {
      throw new UsernameNotFoundException(username);
    }
  }

  private Collection<GrantedAuthority> getAuthorities(Account account) {
    Role role = account.getRole();
    if (role == null || role.getTypeRole() == null) {
      return List.of(new SimpleGrantedAuthority(RoleType.USER.getValue()));
    }
    return List.of(new SimpleGrantedAuthority(role.getTypeRole().getValue()));
  }

  @Override
  @Transactional
  public AuthResponse authenticate(String username, String password) {

    Account account = accountRepository
            .findByUserName(username)
            .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

    if (!passwordEncoder.matches(password, account.getPassWord())) {
      throw new AppException(ErrorCode.USER_PASSWORD_NOT_MATCH);
    }

    log.info("Pass authentication for user: {}", username);

    refreshTokenRepository.revokeAllByAccount(account.getId());

    RefreshToken refreshToken = new RefreshToken();
      refreshToken.setToken(UUID.randomUUID().toString());
      refreshToken.setExpiryDate(
          LocalDateTime.now().plusDays(expiredRefreshToken / (1000 * 60 * 60 * 24)));
      refreshToken.setAccount(account);
      refreshToken.setRevoked(false);

      // save DB for fallback
      refreshTokenRepository.save(refreshToken);

    try {
      String redisKey = "refreshToken:" + refreshToken.getToken();

      Map<String, Object> refreshData = new HashMap<>();
      refreshData.put("accountId", account.getId().toString());
      refreshData.put("username", username);
      refreshData.put("roles", account.getRole().getTypeRole().getValue());
      refreshData.put("device", "web"); // detect from request
      refreshData.put("expiryDate", refreshToken.getExpiryDate().toString());

      redisTemplate.opsForHash().putAll(redisKey, refreshData);
      redisTemplate.expire(redisKey,
              Duration.between(LocalDateTime.now(), refreshToken.getExpiryDate())
      );

      log.info("Cached refresh token in Redis with key={}", redisKey);
    } catch (Exception e) {
      log.warn("Could not cache refresh token in Redis for account {}. Error: {}",
              account.getId(), e.getMessage());
    }

    String accessToken = jwtProvider.generateToken(
            username,
            account.getRole().getTypeRole().getValue(),
            account.getId()
    );

    return new AuthResponse(accessToken, refreshToken.getToken());
  }

  @Override
  @Transactional
  public String signUp(RegisterRequest registerRequest) {
    if (accountRepository.existsByUserName(registerRequest.getUserName())) {
      throw new AppException(ErrorCode.USER_ALREADY_EXISTED);
    }
    Account account = new Account();
    account.setUserName(registerRequest.getUserName());
    account.setPassWord(passwordEncoder.encode(registerRequest.getPassWord()));
    Role role =
        roleRepository
            .findById(2)
            .orElseThrow(() -> new AppException(ErrorCode.COMMON_RESOURCE_NOT_FOUND));
    account.setRole(role);
    accountRepository.saveAndFlush(account);

    User user = new User();
    user.setFullName(account.getUserName());
    user.setAccount(account);
    user.setPhone("000-000-0000"); // Normalize phone number or set a default value
    userRepository.save(user);

    return account.getUserName();
  }

  @Override
  @Transactional
  public AccountDTO createAccount(AccountDTO account) {
    if (accountRepository.existsByUserName(account.getUserName())) {
      throw new AppException(ErrorCode.USER_ALREADY_EXISTED);
    }
    Role role =
        roleRepository
            .findById(account.getRoleId())
            .orElseThrow(() -> new AppException(ErrorCode.COMMON_RESOURCE_NOT_FOUND));
    Account saveAccount = accountMapper.toEntity(account);
    saveAccount.setRole(role);
    accountRepository.save(saveAccount);
    return  accountMapper.toDTO(saveAccount);
  }

  public List<AccountDTO> getAllAccounts() {
    if (accountRepository.findAll().isEmpty()) {
      throw new AppException(ErrorCode.USER_NOT_FOUND);
    }
    List<Account> accounts = accountRepository.findAll();
    List<AccountDTO> accountDTOs = accounts.stream().map(accountMapper::toDTO).toList();
    return  accountDTOs;
  }

  @Override
  public AccountDTO getAccountById(UUID id) {
    Account account =
        accountRepository
            .findById(id)
            .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
    return  accountMapper.toDTO(account);
  }

  @Override
  @Transactional
  public Boolean deleteAccount(UUID userId) {
    Account account =
        accountRepository
            .findById(userId)
            .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
    accountRepository.delete(account);
    return true;
  }

  @Override
  @Transactional
  public Account updateAccount(
      UpdateAccountRequest updateAccountRequest, int accountId) {
    return null;
  }

  public boolean isUsernameExists(String username) {
    Optional<Account> account = accountRepository.findByUserNameIgnoreCase(username);
    return account.isPresent();
  }
}

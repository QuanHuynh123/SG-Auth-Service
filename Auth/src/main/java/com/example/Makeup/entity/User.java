package com.example.Makeup.entity;

import jakarta.persistence.*;
import java.util.List;
import java.util.UUID;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "users")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User extends Base {
  @Id
  @Column
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(name = "full_name", length = 40)
  String fullName;

  @Column(name = "email", length = 40)
  String email;

  @Column(name = "address", length = 50)
  String address;

  @Column(name = "phone", length = 20, nullable = false)
  String phone;

  @Column(name = "is_guest", nullable = false)
  private boolean isGuest = false;

  @Column(name = "guest_token")
  private String guestToken;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "account_id") // Đảm bảo account_id có thể NULL
  Account account;

}

package com.example.Makeup.entity;

import jakarta.persistence.*;
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
@Table(name = "account")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Account extends Base {
  @Id
  @Column
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(name = "username", length = 250, nullable = false, unique = true)
  String userName;

  @Column(name = "password", length = 250, nullable = false)
  String passWord;

  @OneToOne(fetch = FetchType.LAZY, mappedBy = "account", cascade = CascadeType.ALL)
  User user;

  @ManyToOne
  @JoinColumn(name = "role_id", nullable = false)
  Role role;
}

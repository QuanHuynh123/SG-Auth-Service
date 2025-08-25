package com.example.Makeup.entity;

import com.example.Makeup.enums.RoleType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "role")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Role extends Base {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  int id;

  @Enumerated(EnumType.STRING)
  @Column(name = "type_role", length = 40, nullable = false)
  RoleType typeRole;
}

package com.example.Makeup.dto.model;

import java.time.LocalDateTime;
import lombok.*;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoleDTO {
  int id;
  String nameRole;
  LocalDateTime createdAt;
  LocalDateTime updatedAt;
}

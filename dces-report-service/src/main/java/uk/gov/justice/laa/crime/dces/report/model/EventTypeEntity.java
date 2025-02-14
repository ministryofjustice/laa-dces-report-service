package uk.gov.justice.laa.crime.dces.report.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "event_type")
public class EventTypeEntity {
  @Id
  private Integer id;
  private String description;
}

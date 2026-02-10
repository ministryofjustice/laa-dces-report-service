package uk.gov.justice.laa.crime.dces.report.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "case_submission_error")
public class CaseSubmissionErrorEntity {

  @Id
  @SequenceGenerator(name = "case_submission_error_gen_seq", sequenceName = "case_submission_error_id_seq", allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "case_submission_error_gen_seq")
  private Integer id;
  private Integer maatId;
  private Integer concorContributionId;
  private Integer fdcId;
  private String title;
  private Integer status;
  private String detail;
  private LocalDateTime creationDate;

}

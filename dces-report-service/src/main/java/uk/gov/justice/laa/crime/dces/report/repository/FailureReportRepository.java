package uk.gov.justice.laa.crime.dces.report.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import uk.gov.justice.laa.crime.dces.report.dto.FailureReport;
import uk.gov.justice.laa.crime.dces.report.dto.FailureReportKey;

@Repository
public interface FailureReportRepository extends JpaRepository<FailureReport, FailureReportKey> {

  /*
    * This query is used to get the failing MAAT IDs from the case_submission table along with related details (where did it fail: DRC or MAAT; how many attempts have been made and when).
    * 1. The subquery called "failures" gets all case_submission rows for maat IDs for which the latest concor/fdc contribution IDs are not updated to SENT in MAAT DB.
    * 2. These are then "left outer joined" with two other subqueries, one to get successful DRC calls and the other to get successful MAAT updates.
    * 3. The existence of sent_to_drc.maat_id and updated_in_maat.maat_id is used to determine if the DRC and MAAT updates were successful.
    * 4. The results are grouped by maat_id, concor_contribution_id, and fdc_id. SUM and MIN/MAX functions are used to get the count and date details.
   */
  @Query(value = "select failures.maat_id, failures.record_type contribution_type, CASE WHEN failures.record_type = 'Fdc' THEN failures.fdc_id ELSE failures.concor_contribution_id END contribution_id,\n"
      + "        CASE WHEN MAX(sent_to_drc.maat_id) is null then FALSE ELSE TRUE END sent_to_drc,\n"
      + "        SUM(\n"
      + "            case when failures.event_type = 2 THEN 1 ELSE 0 END\n"
      + "        ) drc_send_attempts,\n"
      + "        MIN(\n"
      + "            case when failures.event_type = 2 THEN failures.processed_date ELSE null END\n"
      + "        ) first_drc_attempt_date,\n"
      + "        MAX(\n"
      + "            case when failures.event_type = 2 THEN failures.processed_date ELSE null END\n"
      + "        ) last_drc_attempt_date,\n"
      + "        CASE WHEN MAX(updated_in_maat.maat_id) is null then FALSE ELSE TRUE END updated_in_maat,\n"
      + "        SUM(\n"
      + "            case when failures.event_type = 3 THEN 1 ELSE 0 END\n"
      + "        ) maat_update_attempts,\n"
      + "        MIN(\n"
      + "            case when failures.event_type = 3 THEN failures.processed_date ELSE null END\n"
      + "        ) first_maat_attempt_date,\n"
      + "        MAX(\n"
      + "            case when failures.event_type = 3 THEN failures.processed_date ELSE null END\n"
      + "        ) last_maat_attempt_date\n"
      + "from\n"
      + "    (    select *\n"
      + "          from case_submission\n"
      + "         where \n"
      + "           --Only take the last (max) concor/fdc contribution IDs for each MAAT ID as they supersede any older ones\n"
      + "           (maat_id, record_type, CASE WHEN record_type = 'Fdc' THEN COALESCE(fdc_id, -1) ELSE COALESCE(concor_contribution_id, -1) END) in \n"
      + "           (    select maat_id, record_type, max(CASE WHEN record_type = 'Fdc' THEN COALESCE(fdc_id, -1) ELSE COALESCE(concor_contribution_id, -1) END)\n"
      + "                  from case_submission\n"
      + "                 where event_type=1\n"
      + "                   and http_status = 200\n"
      + "              group by maat_id, record_type\n"
      + "            )\n"
      + "            --only take the records that did not get updated as SENT in MAAT DB, i.e. the failures we want to report on\n"
      + "            and (maat_id, CASE WHEN record_type = 'Fdc' THEN COALESCE(fdc_id, -1) ELSE COALESCE(concor_contribution_id, -1) END) not in \n"
      + "                (\n"
      + "                    select distinct maat_id, CASE WHEN record_type = 'Fdc' THEN COALESCE(fdc_id, -1) ELSE COALESCE(concor_contribution_id, -1) END\n"
      + "                      from case_submission\n"
      + "                     where event_type=3\n"
      + "                       and http_status = 200\n"
      + "                )\n"
      + "    ) failures\n"
      + "    left outer join\n"
      + "    (    select *\n"
      + "          from case_submission\n"
      + "         where event_type=2\n"
      + "           and http_status = 200\n"
      + "           ) sent_to_drc on failures.id = sent_to_drc.id \n"
      + "    left outer join\n"
      + "        (    select *\n"
      + "              from case_submission\n"
      + "             where event_type=3\n"
      + "               and http_status = 200\n"
      + "               ) updated_in_maat  on sent_to_drc.id = updated_in_maat.id \n"
      + "group by failures.maat_id, failures.record_type, failures.concor_contribution_id, failures.fdc_id\n"
      + "\n", nativeQuery = true)
  List<FailureReport> findFailures();

}

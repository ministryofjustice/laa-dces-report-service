package uk.gov.justice.laa.crime.dces.report.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CSVDataLine {

    String maatId;
    String dataFeedType;
    String assessmentDate;
    String ccOutcomeDate;
    String correspondenceSentDate;
    String repOrderStatusDate;
    String hardshipReviewDate;
    String passportedDate;

    public String toString(){
        return maatId + "," +
                dataFeedType + "," +
                assessmentDate + "," +
                ccOutcomeDate + "," +
                correspondenceSentDate + "," +
                repOrderStatusDate + "," +
                hardshipReviewDate + "," +
                passportedDate;


    }

}

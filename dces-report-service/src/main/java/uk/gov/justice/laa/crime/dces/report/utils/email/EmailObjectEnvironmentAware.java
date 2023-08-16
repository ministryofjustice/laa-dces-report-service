package uk.gov.justice.laa.crime.dces.report.utils.email;

public interface EmailObjectEnvironmentAware {

    String getEnvironment();

    void setEnvironment();
}

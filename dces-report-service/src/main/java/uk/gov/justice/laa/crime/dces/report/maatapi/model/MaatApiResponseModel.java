package uk.gov.justice.laa.crime.dces.report.maatapi.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "id",
        "totalFiles"
})
public class MaatApiResponseModel {
    private Integer id;
    private Integer totalFiles;

    public MaatApiResponseModel() {
    }

    public MaatApiResponseModel(Integer id, Integer totalFiles) {
        this.id = id;
        this.totalFiles = totalFiles;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getTotalFiles() {
        return totalFiles;
    }

    public void setTotalFiles(Integer totalFiles) {
        this.totalFiles = totalFiles;
    }

    @Override
    public String toString() {
        return "MaatApiResponse{" +
                "id=" + id +
                ", totalFiles=" + totalFiles +
                '}';
    }
}
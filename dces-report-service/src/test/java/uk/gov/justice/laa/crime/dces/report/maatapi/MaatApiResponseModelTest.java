package uk.gov.justice.laa.crime.dces.report.maatapi;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import uk.gov.justice.laa.crime.dces.report.maatapi.model.MaatApiResponseModel;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class MaatApiResponseModelTest {
    private static final int DEFAULT_ID = 1;
    private static final int DEFAULT_TOTAL = 0;


    @Test
    void givenMaatApiResponse_whenGetIdIsInvoked_thenCorrectIdIsReturned() {
        int expectedId = 3;
        MaatApiResponseModel expectedResponse = new MaatApiResponseModel(
                expectedId, DEFAULT_TOTAL
        );
        assertThat(expectedResponse.getId()).isEqualTo(expectedId);
    }

    @Test
    void givenMaatApiResponse_whenGetTotalFilesIsInvoked_thenCorrectTotalFilesIsReturned() {
        int expectedTotalFiles = 30;
        MaatApiResponseModel expectedResponse = new MaatApiResponseModel(
                DEFAULT_ID, expectedTotalFiles
        );
        assertThat(expectedResponse.getTotalFiles()).isEqualTo(expectedTotalFiles);
    }

    @Test
    void givenMaatApiResponse_whenSetTotalFilesIsInvoked_thenTotalFilesIsUpdated() {
        int expectedTotalFiles = 30;
        MaatApiResponseModel response = new MaatApiResponseModel(
                DEFAULT_ID, DEFAULT_TOTAL
        );
        assertThat(response.getTotalFiles()).isEqualTo(DEFAULT_TOTAL);

        response.setTotalFiles(expectedTotalFiles);

        assertThat(response.getTotalFiles()).isNotEqualTo(DEFAULT_TOTAL);
        assertThat(response.getTotalFiles()).isEqualTo(expectedTotalFiles);
    }

    @Test
    void givenMaatApiResponse_whenSetIdIsInvoked_thenIdIsUpdated() {
        int expectedId = 3;
        MaatApiResponseModel response = new MaatApiResponseModel(
                DEFAULT_ID, DEFAULT_TOTAL
        );
        assertThat(response.getId()).isEqualTo(DEFAULT_ID);

        response.setId(expectedId);

        assertThat(response.getId()).isNotEqualTo(DEFAULT_ID);
        assertThat(response.getId()).isEqualTo(expectedId);
    }

    @Test
    void givenMaatApiResponse_whenToStringInvoked_thenAStringIsReturned() {
        String expectedEstring = String.format("MaatApiResponse{" +
                "id=%s, totalFiles=%s}", DEFAULT_ID, DEFAULT_TOTAL);
        MaatApiResponseModel response = new MaatApiResponseModel(
                DEFAULT_ID, DEFAULT_TOTAL
        );

        assertThat(response.toString()).isInstanceOf(String.class);
        assertThat(response.toString()).isEqualTo(expectedEstring);



    }

}
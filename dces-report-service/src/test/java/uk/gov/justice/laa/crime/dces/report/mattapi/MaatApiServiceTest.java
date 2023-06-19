package uk.gov.justice.laa.crime.dces.report.mattapi;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MaatApiServiceTest {
    @Autowired
    @MockBean
    private MaatApiService maatApiService;

    @BeforeAll
    public void setup() {
    }

    @Test
    void getApiResponseViaGET() {
    }
}
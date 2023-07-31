package uk.gov.justice.laa.crime.dces.report.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import uk.gov.justice.laa.crime.dces.report.service.ContributionFilesService;
import uk.gov.service.notify.NotificationClient;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
class ControllerDateFormatTest {
    private static final String REQUEST_PATH = "/api/internal/v1/dces/report/contributions/%s/%s";

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @MockBean
    private ContributionFilesService mockService;

    @MockBean
    NotificationClient notifyClient;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        Mockito.when(mockService.getFiles(any(), any())).thenReturn(List.of("<xml1>"));
    }

    @Test
    void dateParameterStringFails() throws Exception {
        mockMvc.perform(get(String.format(REQUEST_PATH, "3414", "3243214")))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void dateParameterStringSucceedsWithCorrectFormat() throws Exception {
        mockMvc.perform(get(String.format(REQUEST_PATH, "01.01.2023", "31.01.2023")))
                .andExpect(status().isOk());
    }
}
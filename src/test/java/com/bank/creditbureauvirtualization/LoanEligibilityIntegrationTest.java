
package com.bank.creditbureauvirtualization;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class LoanEligibilityIntegrationTest {

    private static WireMockServer wireMockServer;

    @Autowired
    private MockMvc mockMvc;

    @BeforeAll
    static void startWireMock() {
        wireMockServer = new WireMockServer(8081);
        wireMockServer.start();

        wireMockServer.stubFor(
                WireMock.get(urlEqualTo("/credit-score/CUST101"))
                        .willReturn(
                                aResponse()
                                        .withHeader("Content-Type", "application/json")
                                        .withBody("""
                            {
                              "customerId": "CUST101",
                              "creditScore": 780,
                              "status": "AVAILABLE"
                            }
                            """)
                        )
        );

        wireMockServer.stubFor(
                WireMock.get(urlEqualTo("/credit-score/CUST102"))
                        .willReturn(
                                aResponse()
                                        .withHeader("Content-Type", "application/json")
                                        .withBody("""
                            {
                              "customerId": "CUST102",
                              "creditScore": 650,
                              "status": "AVAILABLE"
                            }
                            """)
                        )
        );


        wireMockServer.stubFor(
                WireMock.get(urlEqualTo("/credit-score-error/CUST101"))
                        .willReturn(
                                aResponse()
                                        .withStatus(500)
                                        .withHeader("Content-Type", "application/json")
                                        .withBody("""
                    {
                      "status": "ERROR",
                      "message": "Simulated credit bureau failure"
                    }
                    """)
                        )
        );

        wireMockServer.stubFor(
                WireMock.get(urlEqualTo("/credit-score-timeout/CUST101"))
                        .willReturn(
                                aResponse()
                                        .withFixedDelay(5000)
                                        .withHeader("Content-Type", "application/json")
                                        .withBody("""
                    {
                      "customerId": "CUST101",
                      "creditScore": 780,
                      "status": "AVAILABLE"
                    }
                    """)
                        )
        );
    }

    @AfterAll
    static void stopWireMock() {
        if (wireMockServer != null) {
            wireMockServer.stop();
        }
    }

    @Test
    void highCreditScoreShouldBeEligible() throws Exception {
        mockMvc.perform(
                        get("/loan-eligibility/CUST101")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.creditScore").value(780))
                .andExpect(jsonPath("$.eligible").value(true));
    }

    @Test
    void lowCreditScoreShouldNotBeEligible() throws Exception {
        mockMvc.perform(
                        get("/loan-eligibility/CUST102")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.creditScore").value(650))
                .andExpect(jsonPath("$.eligible").value(false));
    }

    @Test
    void serverErrorShouldReturn502() throws Exception {
        mockMvc.perform(
                        get("/loan-eligibility/CUST101")
                                .param("scenario", "server-error")
                )
                .andExpect(status().isBadGateway());
    }

    @Test
    void timeoutShouldReturn503() throws Exception {
        mockMvc.perform(
                        get("/loan-eligibility/CUST101")
                                .param("scenario", "timeout")
                )
                .andExpect(status().isServiceUnavailable());
    }

    @Test
    void unknownCustomerShouldReturn404() throws Exception {
        mockMvc.perform(
                        get("/loan-eligibility/CUST999")
                )
                .andExpect(status().isNotFound());
    }

}

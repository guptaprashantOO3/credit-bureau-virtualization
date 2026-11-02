
package com.bank.creditbureauvirtualization;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

import com.github.tomakehurst.wiremock.WireMockServer;

public class VirtualCreditBureau {

    public static void main(String[] args) {

        WireMockServer server = new WireMockServer(8081);
        server.start();

        // Customer with a high credit score
        server.stubFor(
                get(urlEqualTo("/credit-score/CUST101"))
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

        // Customer with a low credit score
        server.stubFor(
                get(urlEqualTo("/credit-score/CUST102"))
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

        // Customer with a medium credit score
        server.stubFor(
                get(urlEqualTo("/credit-score/CUST103"))
                        .willReturn(
                                aResponse()
                                        .withHeader("Content-Type", "application/json")
                                        .withBody("""
                            {
                              "customerId": "CUST103",
                              "creditScore": 720,
                              "status": "AVAILABLE"
                            }
                            """)
                        )
        );

        // Unknown customers
        server.stubFor(
                get(urlMatching("/credit-score/.*"))
                        .atPriority(10)
                        .willReturn(
                                aResponse()
                                        .withStatus(404)
                                        .withHeader("Content-Type", "application/json")
                                        .withBody("""
                            {
                              "status": "NOT_FOUND",
                              "message": "Customer record not found"
                            }
                            """)
                        )
        );


        // Scenario A: Simulate server failure
        server.stubFor(
                get(urlPathMatching("/credit-score-error/.*"))
                        .willReturn(
                                aResponse()
                                        .withStatus(500)
                                        .withHeader("Content-Type", "application/json")
                                        .withBody("""
                            {
                              "status": "ERROR",
                              "message": "Credit bureau internal server error"
                            }
                            """)
                        )
        );

        // Scenario B: Simulate a slow response
        server.stubFor(
                get(urlPathMatching("/credit-score-timeout/.*"))
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


        System.out.println(
                "Virtual Credit Bureau running on port 8081"
        );
    }
}

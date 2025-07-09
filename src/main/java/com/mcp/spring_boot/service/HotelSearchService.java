package com.mcp.spring_boot.service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.mcp.spring_boot.params.HotelSearchParams;

@Service
public class HotelSearchService {
    private static final Logger log = LoggerFactory.getLogger(HotelSearchService.class);
    private final WebClient webClient;

    public HotelSearchService(WebClient webClient) {
        this.webClient = webClient;
    }

    @Tool(name = "hotel_search_tool", description = "Searches for hotels. All fields required except currency.")
public String search(HotelSearchParams params) {
        try {
            return webClient.post()
                    .uri("/royal/hotel/search")
                    .bodyValue(params)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block(); // blocks to return String directly for tool use
        } catch (org.springframework.web.reactive.function.client.WebClientResponseException e) {
            log.error("API error: {}", e.getResponseBodyAsString());
            return "Error during hotel search: " + e.getMessage() + " - " + e.getResponseBodyAsString();
        } catch (Exception e) {
            log.error("API error: {}", e.getMessage());
            return "Error during hotel search: " + e.getMessage();
        }
    }
}

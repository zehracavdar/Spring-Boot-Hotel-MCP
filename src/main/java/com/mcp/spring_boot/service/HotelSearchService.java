package com.mcp.spring_boot.service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
public class HotelSearchService {
    private static final Logger log = LoggerFactory.getLogger(HotelSearchService.class);
    private final WebClient webClient;

    public HotelSearchService(WebClient webClient) {
        this.webClient = webClient;
    }

    @Tool(
        name = "hotel_search_tool",
        description = "Searches for hotels using check-in/out dates, location, guest counts, and preferences."
    )
    public String hotelSearch(Map<String, Object> params) {
        String sessionId = (String) params.remove("sessionId");
        try {

            
            return webClient.post()
                    .uri("/royal/hotel/search")
                    .bodyValue(params)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block(); // blocks to return String directly for tool use
        } catch (Exception e) {
            return "Error during hotel search: " + e.getMessage();
        }
    }
}

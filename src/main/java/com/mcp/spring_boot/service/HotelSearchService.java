package com.mcp.spring_boot.service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import com.mcp.spring_boot.params.HotelSearchParams;
import com.mcp.spring_boot.params.LocationHotelSearchParams;
import com.mcp.spring_boot.params.LocationHotelSearchRequest;
import com.mcp.spring_boot.service.helper.HotelSearchServiceMethods;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class HotelSearchService {
    private static final String FEED_ID = "1714d37c-2a14-460d-8344-cdff5cf02018";
    private static final int LIMIT = 10;
    private static final int OFFSET = 300;

    private static final Logger log = LoggerFactory.getLogger(HotelSearchService.class);
    private final WebClient webClient;
    private final HotelSearchServiceMethods helper;
    public HotelSearchService(WebClient webClient, HotelSearchServiceMethods helper) {
        this.webClient = webClient;
        this.helper = helper;
    }

/*   @Tool(name = "hotel_search_tool", description = "Searches for hotels. All fields required except currency.")
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
    }*/ 
    @Tool(
    name        = "location_hotel_search_tool",
    description = "Search hotels by city name, check-in/out, and guest info. feedId and limit, offset are set internally."
)
public String searchByCity(LocationHotelSearchParams params) {

    Integer locationId = helper.getLocationIdByQuery(params.getCity());
    if (locationId == null) {
        throw new RuntimeException("No location ID found for city: " + params.getCity());
    }

    // Build the typed request object
    LocationHotelSearchRequest requestBody = LocationHotelSearchRequest.builder()
            .checkIn(params.getCheckIn())
            .checkOut(params.getCheckOut())
            .clientNationality(params.getClientNationality())
            .rooms(params.getRooms())
            .allPricesFlag(params.getAllPricesFlag())
            .limit(LIMIT)
            .offset(OFFSET)
            .feedId(FEED_ID)           // injected
            .locationId(locationId)    // injected
            .build();

    try {
        return webClient.post()
                .uri("/generic-api-service/royal/hotel/search-by-location")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    } catch (Exception e) {
        log.error("Hotel search failed: {}", e.getMessage());
        return (e.getMessage());
    }
}
}

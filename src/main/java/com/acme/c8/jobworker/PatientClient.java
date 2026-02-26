package com.acme.c8.jobworker;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * HTTP client utility for loading patient data from the CapBPM API.
 * Provides methods to fetch patient information with pagination support.
 *
 * @since 1.0.0
 */
public final class PatientClient {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();

    private static final String API_BASE_URL = "https://api.capbpm.com/api/patients/load";
    private static final String PAGE_PARAM = "page";
    private static final String SIZE_PARAM = "size";
    private static final String CONTENT_KEY = "content";
    private static final int SUCCESS_STATUS_CODE = 200;

    /**
     * Private constructor to prevent instantiation of utility class.
     */
    private PatientClient() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /**
     * Loads patients from the API with pagination support.
     * Extracts the "content" array from the JSON response.
     *
     * @param page the page number (0-indexed)
     * @param size the number of patients per page
     * @return a list of patient maps containing patient data
     * @throws IOException if an I/O error occurs during the HTTP request
     * @throws InterruptedException if the HTTP request is interrupted
     * @throws IllegalStateException if the API response is invalid or unsuccessful
     * @throws NullPointerException if response body is null
     */
    public static List<Map<String, Object>> loadPatients(int page, int size) throws IOException, InterruptedException {

        String url = buildUrl(page, size);
        HttpRequest request = buildRequest(url);

        HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());

        validateResponse(response);

        return extractPatientList(response.body());
    }

    /**
     * Builds the API URL with page and size parameters.
     *
     * @param page the page number
     * @param size the page size
     * @return the complete URL string
     */
    private static String buildUrl(int page, int size) {
        return String.format("%s?%s=%d&%s=%d", API_BASE_URL, PAGE_PARAM, page, SIZE_PARAM, size);
    }

    /**
     * Builds an HTTP GET request for the specified URL.
     *
     * @param url the URL to request
     * @return the constructed HTTP request
     */
    private static HttpRequest buildRequest(String url) {
        return HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();
    }

    /**
     * Validates the HTTP response status code.
     *
     * @param response the HTTP response to validate
     * @throws IllegalStateException if the response status is not 200 (OK)
     */
    private static void validateResponse(HttpResponse<String> response) {
        if (response.statusCode() != SUCCESS_STATUS_CODE) {
            throw new IllegalStateException(
                    String.format("Failed to load patients. HTTP %d", response.statusCode())
            );
        }
    }

    /**
     * Extracts the patient list from the JSON response body.
     * Parses the response and extracts the "content" array.
     *
     * @param responseBody the JSON response body
     * @return a list of patient maps
     * @throws IllegalStateException if the response does not contain a valid "content" array
     */
    private static List<Map<String, Object>> extractPatientList(String responseBody) {
        try {
            JsonNode root = OBJECT_MAPPER.readTree(responseBody);
            JsonNode contentNode = root.get(CONTENT_KEY);

            if (contentNode == null || !contentNode.isArray()) {
                throw new IllegalStateException(
                        "Response does not contain a valid '" + CONTENT_KEY + "' array"
                );
            }

            return OBJECT_MAPPER.convertValue(
                    contentNode,
                    new TypeReference<>() {}
            );
        } catch (IOException e) {
            throw new IllegalStateException("Failed to parse patient response", e);
        }
    }
}

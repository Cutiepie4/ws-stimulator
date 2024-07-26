package com.viettel.vda.wsstimulator.controller;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NcdnBccs {

    @PostMapping("/ncdn/bccs")
    public ResponseEntity<?> handleRequest(@RequestBody JsonNode requestData) {
        // Check for missing attributes and return an error response if any are missing
        if (!requestData.hasNonNull("client_id") ||
                !requestData.hasNonNull("client_secret") ||
                !requestData.hasNonNull("msisdn") ||
                !requestData.hasNonNull("tonecode") ||
                !requestData.hasNonNull("tonename") ||
                !requestData.hasNonNull("tone_id") ){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(com.viettel.vda.wsstimulator.responsedto.NcdnBccs.builder().errorCode("000000").build());
        }

        String clientId = requestData.get("client_id").asText();
        String clientSecret = requestData.get("client_secret").asText();
        String msisdn = requestData.get("msisdn").asText();
        String tonecode = requestData.get("tonecode").asText();
        String tonename = requestData.get("tonename").asText();
        String toneId = requestData.get("tone_id").asText();
        String brandId = requestData.get("brand_id").asText();

        return ResponseEntity.ok(com.viettel.vda.wsstimulator.responsedto.NcdnBccs.builder().errorCode("000000").build());
    }
}

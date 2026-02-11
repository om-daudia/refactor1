package com.acme.c8.integration;


import com.acme.c8.configuration.CAPBPMFeignConfig;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "capBPMClient",
        url = "${capbpm.uri}",
        configuration = CAPBPMFeignConfig.class
)
public interface CAPBPMClient {

    @PostMapping("/patients/load")
    JsonNode getPatients(@RequestParam("page") Integer page, @RequestParam("size") Integer size);
}

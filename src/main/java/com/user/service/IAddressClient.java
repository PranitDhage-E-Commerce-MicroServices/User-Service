package com.user.service;

import com.user.models.response.APIResponseEntity;
import com.user.pojo.Address;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

//@FeignClient(url = "http://localhost:8082/address", value = "Address-Client")
@FeignClient(name = "ADDRESS-SERVICE")
public interface IAddressClient {

    @GetMapping(
            value = "/address/list/{userId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    APIResponseEntity<List<Address>> getAddresses(
            @Parameter(description = "User Identifier", required = true) @PathVariable("userId") int userId
    );
}

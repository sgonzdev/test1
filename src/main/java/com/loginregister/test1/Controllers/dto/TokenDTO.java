package com.loginregister.test1.Controllers.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TokenDTO(
    @JsonProperty("access_token")
    String accessToken,
    @JsonProperty("refresh_token")
    String refreshToken
) {
}

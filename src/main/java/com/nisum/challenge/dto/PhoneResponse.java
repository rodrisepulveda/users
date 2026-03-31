package com.nisum.challenge.dto;

public record PhoneResponse(
    String number,
    String cityCode,
    String countryCode
) {}

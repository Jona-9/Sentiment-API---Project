package com.project.sentimentapi.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SentimentsResponseDto {
    List<ResponseDto> results;
    private Integer total;
}

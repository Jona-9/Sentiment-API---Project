package com.project.sentimentapi.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SentimentsResponseDto {
    private List<ResponseDto> results;
    private Integer total;
}

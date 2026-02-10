package com.hirehub.hirehub_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryApplicationDto {
    private String category;
    private Long applicationCount;
    private Long interviewCount;
    private Long offerCount;
    private Double successRate;
}






package com.hirehub.hirehub_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResponseDto {
        private String message;
        private String status;
        
        public ResponseDto(String s) {
            this.message = s;
        }

}

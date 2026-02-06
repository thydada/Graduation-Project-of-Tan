package com.example.gpot.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FormalPackageExceptionRequest {

    private Long packageId;

    private String exceptionType;

    private String exceptionReason;

    private Long employeeId;

    private String source;
}

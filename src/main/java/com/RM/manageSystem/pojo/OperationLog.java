package com.RM.manageSystem.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OperationLog {
    private Integer id;
    private Integer operationUser;
    private LocalDateTime operationTime;
    private String operationClassName;
    private String operationFunctionName;
    private String operationParams;
    private String operationReturnValue;
    private Long operationDuration;
}

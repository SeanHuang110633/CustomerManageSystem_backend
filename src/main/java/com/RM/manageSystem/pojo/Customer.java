package com.RM.manageSystem.pojo;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.groups.Default;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Customer {
    @NotNull(groups = Customer.Update.class)
    private Integer id;
    @NotEmpty
    private String customerName;
    @NotNull(groups = Customer.Add.class)
    private Character gender;
    private Integer birthYear;
    private String phoneNumber;
    @Email
    private String email;
    private String frequency;  //上課頻率
    private String approach;  // 知道這間工作室的管道
    @DateTimeFormat
    private LocalDate firstLesson;  //第一次上課日期
    @DateTimeFormat
    private LocalDate lastLesson;  //最近一次上課日期
    private Integer totalLessons;  //總上課數(預設為1)
    private Integer coachId; //預設為1(老闆)
    private Integer remainingLessons;  //剩餘課堂數(預設為0)
    //以下預設為無
    private String medicalHistory;  //過往病史
    private String medication;  //用藥情況
    private String symptoms;  //身體狀況
    private String symptomCauses;
    private String transportationHabits;
    private String exerciseHabits;
    //在service層添加
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Integer isDelete;

    // 作為更新操作的驗證分組
    public interface Update extends Default {}

    public interface Add extends Default{}
}

package com.ubi.masterservice.dto.user;

import java.time.LocalDate;

import javax.validation.constraints.Pattern;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContactInfoDto {
	
	private String firstName;
    private String middleName;
    private String lastName;
    private String email;
    private String contactNumber;
    private String city;
    private String state;
    private String address;
    private Integer age;
    private String gender;

    @JsonFormat(pattern="yyyy-MM-dd")
    private LocalDate dob;
    private String bloodGroup;

    @Pattern(regexp  ="^\\d{12}$",message = "Please Enter a Valid Aadhar Card Number")
    private String aadharCardNumber;
    private String nationality;

}

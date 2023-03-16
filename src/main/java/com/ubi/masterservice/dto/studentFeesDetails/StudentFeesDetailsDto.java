package com.ubi.masterservice.dto.studentFeesDetails;

import com.ubi.masterservice.model.Auditable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StudentFeesDetailsDto extends Auditable {

    private Long id;

    private Long studentFeesId;

    private String feesName;

    private BigDecimal feesAmount;

    private String exemptionName;

    private BigDecimal exemptedAmount;

    private BigDecimal totalAmountPayable;
}

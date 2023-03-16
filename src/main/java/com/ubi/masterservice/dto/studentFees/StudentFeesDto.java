package com.ubi.masterservice.dto.studentFees;


import com.ubi.masterservice.dto.studentFeesDetails.StudentFeesDetailsDto;
import com.ubi.masterservice.model.Auditable;
import lombok.*;
import java.math.BigDecimal;
import java.util.List;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StudentFeesDto extends Auditable {

    private Long id;

    private Long studentId;

    private Integer schoolId;

    private Integer classId;

    private BigDecimal totalFeesAmount;

    private BigDecimal totalExemptedAmount;

    private BigDecimal totalArrears;

    private BigDecimal totalAmountPayable;

    private Boolean isPaid;

    private Boolean isRefunded;

    private Boolean isPaidOffline;

    private String calculationPeriod;

    private String paymentCycle;

    private List<StudentFeesDetailsDto> feesDetailsList;

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + Math.toIntExact(id);
        return result;
    }
}


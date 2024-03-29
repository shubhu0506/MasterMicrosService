package com.ubi.masterservice.entity;
import java.util.Set;

import javax.persistence.*;
import javax.validation.constraints.Email;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import com.ubi.masterservice.model.Auditable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "School_Details")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class School extends Auditable {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int schoolId;

	@Column(name = "schoolCode")
	private int code;

	@Column(name = "schoolName")
	private String name;
	
	@Email
	@Column(name = "schoolEmail")
	private String email;

	@Column(name ="schoolContact")
	private long contact;

	@Column(name ="schoolAddress")
	private String address;

	@Column(name ="schoolType")
	private String type;

	private int strength;

	@Column(name ="schoolShift")
	private String shift;

	@Column(name = "isCollege")
	private Boolean isCollege;	
	
	@Column(name = "exemptionFlag")
	private boolean exemptionFlag;

	@Column(name = "vvnAccount")
	private int vvnAccount;

	@Column(name = "vvnFund")
	private int vvnFund;

	
	private String feesCollectionType;
	
	private int feesCollectionPeriod;

	@Column
	private Long principalId;

	@ManyToOne
	@JoinColumn(name="region_id",referencedColumnName="id" )
	private Region region;

	@OneToMany(fetch= FetchType.EAGER,cascade = CascadeType.PERSIST, mappedBy = "school")
	private Set<ClassDetail> classDetail;

	@ManyToOne
	@JoinColumn(name = "educationalInstitute_id", referencedColumnName = "id",nullable = true)
	private EducationalInstitution educationalInstitution;

	@Override
	public int hashCode() {
		int result = super.hashCode();
		result = 31 * result + schoolId;
		return result;
	}

}

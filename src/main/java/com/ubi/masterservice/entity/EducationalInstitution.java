package com.ubi.masterservice.entity;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
@Builder
public class EducationalInstitution {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;

	private String educationalInstitutionCode;

	private String educationalInstitutionName;

	private String educationalInstitutionType;

	private Long strength;

	private String state;

	private String exemptionFlag;

	private Long vvnAccount;

	private Long adminId;

	@ManyToMany(fetch = FetchType.EAGER,cascade = CascadeType.PERSIST)
	@JoinTable(name = "EI_REGION_TABLE", joinColumns = {
			@JoinColumn(name = "educationalInstitution_id", referencedColumnName = "id") }, inverseJoinColumns = {
			@JoinColumn(name = "region_id", referencedColumnName = "id") })
	private Set<Region> region;

	@OneToMany(fetch = FetchType.EAGER,cascade = CascadeType.PERSIST, mappedBy = "educationalInstitution")
	private Set<School> school;

}

package com.ubi.MasterService.entity;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@ToString
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
	
	
	@ManyToMany(cascade=CascadeType.MERGE)
	@JoinTable(name="EI_REGION_TABLE",
	joinColumns= {
			@JoinColumn(name="educationalInstitution_id",referencedColumnName="id")
	},
	inverseJoinColumns= {
			@JoinColumn(name="region_id",referencedColumnName="id")
	})
	@JsonIgnore
	private Set<Region> region;
	
	
	
	
	
}

package com.ubi.masterservice.entity;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;



@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Region {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;

	//	@Column(name="regionCode")
	private String code;

	//	@Column(name="regionName")
	private String name;

	@JsonIgnore
	@ManyToMany(mappedBy="region",cascade = CascadeType.MERGE)
	private Set<EducationalInstitution> educationalInstitiute;

	@OneToMany(mappedBy="region" , cascade=CascadeType.MERGE)
	private Set<School> school;

}
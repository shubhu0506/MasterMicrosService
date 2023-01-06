package com.ubi.masterservice.entity;

import java.util.Set;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@ToString
public class Region {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;

	//	@Column(name="regionCode")
	private String code;

	//	@Column(name="regionName")
	private String name;

	@JsonIgnore
	@ManyToMany(fetch = FetchType.EAGER,mappedBy="region",cascade = CascadeType.MERGE)
	private Set<EducationalInstitution> educationalInstitiute;

	@OneToMany(fetch = FetchType.EAGER,mappedBy="region" , cascade=CascadeType.MERGE)
	private Set<School> school;

}
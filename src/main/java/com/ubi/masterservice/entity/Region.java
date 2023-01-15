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
public class Region {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;

	private String code;

	private String name;

	@JsonIgnore
	@ManyToMany(fetch = FetchType.EAGER,mappedBy="region", cascade = {CascadeType.PERSIST,CascadeType.REMOVE})
	private Set<EducationalInstitution> educationalInstitiute;

	@OneToMany(fetch = FetchType.EAGER,mappedBy="region" , cascade = {CascadeType.PERSIST,CascadeType.REMOVE})
	private Set<School> school;

}
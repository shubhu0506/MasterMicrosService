package com.ubi.masterservice.entity;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import com.ubi.masterservice.model.Auditable;
import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Region extends Auditable {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;

	//	@Column(name="regionCode")
	private String code;

	//	@Column(name="regionName")
	private String name;

	@Column(name = "adminId")
	private Long adminId;

	@JsonIgnore
	@ManyToMany(fetch = FetchType.EAGER,mappedBy="region",cascade = CascadeType.PERSIST)
	private Set<EducationalInstitution> educationalInstitiute = new HashSet<>();

	@OneToMany(fetch = FetchType.EAGER,mappedBy="region" , cascade=CascadeType.PERSIST)
	private Set<School> school = new HashSet<>();

	@Override
	public int hashCode() {
		int result = super.hashCode();
		result = 31 * result + id;
		return result;
	}
}
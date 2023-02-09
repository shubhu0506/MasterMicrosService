package com.ubi.masterservice.entity;

import java.util.Set;
import javax.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.ubi.masterservice.model.Auditable;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
@Builder
public class EducationalInstitution extends Auditable {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;

	private String educationalInstitutionCode;

	private String educationalInstitutionName;

	private String educationalInstitutionType;

	@Column(name="educational_institution_strength")
	private Long strength;

	private String state;

	@Column(name="educational_institution_exemptionFlag")
	private String exemptionFlag;

	@Column(name="educational_institution_vvnAccount")
	private Long vvnAccount;

	@Column(name = "educational_adminId")
	private Long adminId;

	@Override
	public int hashCode() {
		int result = super.hashCode();
		result = 31 * result + id;
		return result;
	}

	@ManyToMany(fetch = FetchType.EAGER,cascade = CascadeType.PERSIST)
	@JoinTable(name = "EI_REGION_TABLE", joinColumns = {
			@JoinColumn(name = "educationalInstitution_id", referencedColumnName = "id") }, inverseJoinColumns = {
			@JoinColumn(name = "region_id", referencedColumnName = "id") })
	private Set<Region> region;

	@OneToMany(fetch = FetchType.EAGER,cascade = CascadeType.PERSIST, mappedBy = "educationalInstitution")
	private Set<School> school;

}

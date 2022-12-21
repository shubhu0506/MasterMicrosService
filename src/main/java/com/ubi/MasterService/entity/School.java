package com.ubi.MasterService.entity;
import java.util.List;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

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
@ToString
@Table(name = "School_Details")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class School {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
    private int schoolId;
	
	@Column(name = "schoolCode")
	private int code;
	
	@Column(name = "schoolName")
	private String name;
	
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
	
	@Column(name = "exemptionFlag")
	private boolean exemptionFlag;

	@Column(name = "vvnAccount")
	private int vvnAccount;
	
	@Column(name = "vvnFund")
	private int vvnFund;
	

	@ManyToOne
	@JoinColumn(name="region_id",referencedColumnName="id" )
	private Region region;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "school")
	private List<ClassDetail> classDetail;
}

package com.ubi.MasterService.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "class_data ")
public class ClassDetail
{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ClassId")
	private Long classId;

	@Column(name = "ClassCode")
	private String classCode;
 
	@Column(name = "ClassName")
	private String className;

	@ManyToOne
	@JoinColumn(name = "schoolId" )
	private School school;
	
	@OneToMany(mappedBy = "classDetail")	
	List<Student> students = new ArrayList<>();
}







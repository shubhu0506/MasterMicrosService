package com.ubi.masterservice.entity;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.*;

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
@Table(name = "class_data ")
public class ClassDetail
{

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "ClassId")
	private Long classId;

	@Column(name = "ClassCode")
	private String classCode;

	@Column(name = "ClassName")
	private String className;

	@ManyToOne
	@JoinColumn(name = "schoolId")
	private School school;


	@OneToMany(fetch= FetchType.EAGER,mappedBy = "classDetail",cascade = CascadeType.ALL)
	Set<Student> students = new HashSet<>();
}







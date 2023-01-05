package com.ubi.masterservice.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.ubi.masterservice.dto.pagination.PaginationResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.ubi.masterservice.dto.classDto.ClassDto;
import com.ubi.masterservice.dto.classDto.ClassStudentDto;
import com.ubi.masterservice.dto.response.Response;
import com.ubi.masterservice.dto.studentDto.StudentDto;
import com.ubi.masterservice.entity.ClassDetail;
import com.ubi.masterservice.entity.School;
import com.ubi.masterservice.entity.Student;
import com.ubi.masterservice.error.CustomException;
import com.ubi.masterservice.error.HttpStatusCode;
import com.ubi.masterservice.error.Result;
import com.ubi.masterservice.mapper.ClassMapper;
import com.ubi.masterservice.mapper.SchoolMapper;
import com.ubi.masterservice.mapper.StudentMapper;
import com.ubi.masterservice.repository.ClassRepository;
import com.ubi.masterservice.repository.SchoolRepository;
import com.ubi.masterservice.repository.StudentRepository;

@Service
public class ClassServiceImpl implements ClassService {

	private static final Logger LOGGER = LoggerFactory.getLogger(ClassServiceImpl.class);

	@Autowired
	private ClassRepository classRepository;

	@Autowired
	private SchoolRepository schoolRepository;

	@Autowired
	private StudentRepository studentRepository;

	@Autowired
	private ClassMapper classMapper;

	@Autowired
	private SchoolMapper schoolMapper;

	@Autowired
	private StudentMapper studentMapper;


	public Response<ClassStudentDto> addClassDetails(ClassDto classDto) {

		Result<ClassStudentDto> res = new Result<>();
		Response<ClassStudentDto> response = new Response<>();

		ClassDetail className = classRepository.getClassByclassName(classDto.getClassName());
		ClassDetail classCode = classRepository.getClassByclassCode(classDto.getClassCode());

		School school  = schoolRepository.getReferenceById(classDto.getSchoolId());

		if(school != null && school.getClassDetail()!=null) {
			for(ClassDetail classDetail:school.getClassDetail()) {
				if(classDetail.getClassName().equals(classDto.getClassName())){
					throw new CustomException(HttpStatusCode.RESOURCE_ALREADY_EXISTS.getCode(),
							HttpStatusCode.RESOURCE_ALREADY_EXISTS, HttpStatusCode.RESOURCE_ALREADY_EXISTS.getMessage(), res);
				}
			}
		}

		ClassDetail classDetail=new ClassDetail();
		//classDetail.setClassId(classDto.getClassId());
		classDetail.setClassName(classDto.getClassName());
		classDetail.setClassCode(classDto.getClassCode());
		classDetail.setSchool(school);
		classDetail.setStudents(new HashSet<>());

		ClassDetail savedClass=classRepository.save(classDetail);
		ClassStudentDto classStudentDto=classMapper.toStudentDto(savedClass);
		response.setStatusCode(HttpStatusCode.RESOURCE_CREATED_SUCCESSFULLY.getCode());
		response.setMessage(HttpStatusCode.RESOURCE_CREATED_SUCCESSFULLY.getMessage());
		response.setResult(new Result<ClassStudentDto>(classStudentDto));
		return response;
	}

	public Response<PaginationResponse<List<ClassStudentDto>>> getClassDetails(Integer PageNumber, Integer PageSize) {

		Result<PaginationResponse<List<ClassStudentDto>>> allClasses = new Result<>();
		Pageable pageing = PageRequest.of(PageNumber, PageSize);
		Response<PaginationResponse<List<ClassStudentDto>>> getListofClasses = new Response<PaginationResponse<List<ClassStudentDto>>>();

		Page<ClassDetail> classList = this.classRepository.findAll(pageing);
		List<ClassStudentDto> classDto =new ArrayList();

		for(ClassDetail classDetail:classList)
		{
			ClassStudentDto classStudentDto=new ClassStudentDto();
			classStudentDto.setClassDto(classMapper.entityToDto(classDetail));
			classStudentDto.setSchoolDto(schoolMapper.entityToDto(classDetail.getSchool()));

			//Set<Student> s1=classDetail.getStudents();
			Set<StudentDto> studentDto=classDetail.getStudents().stream()
					.map(students -> studentMapper.entityToDto(students)).collect(Collectors.toSet());
			classStudentDto.setStudentDto(studentDto);
			classDto.add(classStudentDto);
		}
		if (classList.isEmpty()) {
			throw new CustomException(HttpStatusCode.RESOURCE_NOT_FOUND.getCode(), HttpStatusCode.RESOURCE_NOT_FOUND,
					HttpStatusCode.RESOURCE_NOT_FOUND.getMessage(), allClasses);
		}

		PaginationResponse paginationResponse=new PaginationResponse<List<ClassStudentDto>>(classDto,classList.getTotalPages(),classList.getTotalElements());


		allClasses.setData(paginationResponse);
		getListofClasses.setStatusCode(HttpStatusCode.CLASS_RETREIVED_SUCCESSFULLY.getCode());
		getListofClasses.setMessage(HttpStatusCode.CLASS_RETREIVED_SUCCESSFULLY.getMessage());
		getListofClasses.setResult(allClasses);
		return getListofClasses;
	}

	public Response<ClassStudentDto> getClassById(Long classid) {

		Response<ClassStudentDto> getClass = new Response<>();
		Optional<ClassDetail> classDetail = this.classRepository.findById(classid);
		Result<ClassStudentDto> classResult = new Result<>();
		if (!classDetail.isPresent()) {
			throw new CustomException(HttpStatusCode.NO_CLASS_MATCH_WITH_ID.getCode(),
					HttpStatusCode.NO_CLASS_MATCH_WITH_ID, HttpStatusCode.NO_CLASS_MATCH_WITH_ID.getMessage(),
					classResult);
		}

		ClassStudentDto classStudentDto=new ClassStudentDto();
		classStudentDto.setClassDto(classMapper.entityToDto(classDetail.get()));
		classStudentDto.setSchoolDto(schoolMapper.entityToDto(classDetail.get().getSchool()));
		Set<StudentDto> studentDto=classDetail.get().getStudents().stream()
				.map(students -> studentMapper.entityToDto(students)).collect(Collectors.toSet());
		classStudentDto.setStudentDto(studentDto);
		;
		classResult.setData(classStudentDto);
		getClass.setStatusCode(HttpStatusCode.CLASS_RETREIVED_SUCCESSFULLY.getCode());
		getClass.setMessage(HttpStatusCode.CLASS_RETREIVED_SUCCESSFULLY.getMessage());
		getClass.setResult(classResult);
		return getClass;
	}

	public Response<ClassDto> deleteClassById(Long id) {
		Result<ClassDto> res = new Result<>();
		Response<ClassDto> response = new Response<>();
		Optional<ClassDetail> classes = classRepository.findById(id);
		if (!classes.isPresent()) {
			throw new CustomException(HttpStatusCode.RESOURCE_NOT_FOUND.getCode(), HttpStatusCode.RESOURCE_NOT_FOUND,
					HttpStatusCode.RESOURCE_NOT_FOUND.getMessage(), res);
		}

		School school=classes.get().getSchool();
		school.getClassDetail().remove(classes.get());
		schoolRepository.save(school);

		for(Student studentId: classes.get().getStudents())
		{
			studentId.setClassDetail(null);
			studentRepository.save(studentId);
		}

		classRepository.deleteById(id);
		response.setMessage(HttpStatusCode.CLASS_DELETED_SUCCESSFULLY.getMessage());
		response.setStatusCode(HttpStatusCode.CLASS_DELETED_SUCCESSFULLY.getCode());
		response.setResult(new Result<ClassDto>(classMapper.entityToDto(classes.get())));
		return response;
	}

	public Response<ClassStudentDto> updateClassDetails(ClassDto classDetailDto) {
		Result<ClassStudentDto> res = new Result<>();
		Response<ClassStudentDto> response = new Response<>();
		Optional<ClassDetail> existingClassContainer = classRepository.findById(classDetailDto.getClassId());
		if (!existingClassContainer.isPresent()) {
			throw new CustomException(HttpStatusCode.NO_CLASS_FOUND.getCode(), HttpStatusCode.NO_CLASS_FOUND,
					HttpStatusCode.NO_CLASS_FOUND.getMessage(), res);
		}
		ClassDto existingClassDetail=classMapper.entityToDto(existingClassContainer.get());
		existingClassDetail.setClassId(classDetailDto.getClassId());
		existingClassDetail.setClassName(classDetailDto.getClassName());
		existingClassDetail.setClassCode(classDetailDto.getClassCode());
		existingClassDetail.setSchoolId(classDetailDto.getSchoolId());


		ClassDetail classDetail1=classMapper.dtoToEntity(existingClassDetail);
		ClassDetail updatedClassDetail=classRepository.save(classDetail1);
		ClassStudentDto classStudentDto=classMapper.toStudentDto(updatedClassDetail);
		res.setData(classStudentDto);
		response.setMessage(HttpStatusCode.CLASS_UPDATED.getMessage());
		response.setStatusCode(HttpStatusCode.CLASS_UPDATED.getCode());
		response.setResult(res);
		return response;
	}

	@Override
	public Response<ClassStudentDto> getClassByName(String className) {

		Response<ClassStudentDto> getClass = new Response<ClassStudentDto>();
		ClassDetail classDetail = this.classRepository.getClassByclassName(className);
		Result<ClassStudentDto> classResult = new Result<>();
		if (classDetail == null) {
			throw new CustomException(HttpStatusCode.CLASS_NOT_FOUND.getCode(), HttpStatusCode.CLASS_NOT_FOUND,
					HttpStatusCode.CLASS_NOT_FOUND.getMessage(), classResult);
		}
		ClassStudentDto classStudentDto=new ClassStudentDto();
		classStudentDto.setClassDto(classMapper.entityToDto(classDetail));
		classStudentDto.setSchoolDto(schoolMapper.entityToDto(classDetail.getSchool()));
		Set<StudentDto> studentDto=classDetail.getStudents().stream()
				.map(students -> studentMapper.entityToDto(students)).collect(Collectors.toSet());
		classStudentDto.setStudentDto(studentDto);
		classResult.setData(classStudentDto);
		getClass.setStatusCode(HttpStatusCode.CLASS_RETREIVED_SUCCESSFULLY.getCode());
		getClass.setMessage(HttpStatusCode.CLASS_RETREIVED_SUCCESSFULLY.getMessage());
		getClass.setResult(classResult);
		return getClass;
	}

	@Override
	public Response<List<ClassDto>> getClasswithSort(String field) {

		Result<List<ClassDto>> allClassResult = new Result<>();

		Response<List<ClassDto>> getListofClasses = new Response<>();

		List<ClassDetail> list = this.classRepository.findAll(Sort.by(Sort.Direction.ASC, field));
		List<ClassDto> classDtos = classMapper.entitiesToDtos(list);

		if (list.size() == 0) {
			throw new CustomException(HttpStatusCode.NO_CLASS_FOUND.getCode(), HttpStatusCode.NO_CLASS_FOUND,
					HttpStatusCode.NO_CLASS_FOUND.getMessage(), allClassResult);
		}
		allClassResult.setData(classDtos);
		getListofClasses.setStatusCode(HttpStatusCode.CLASS_RETRIVED_SUCCESSFULLY.getCode());
		getListofClasses.setMessage(HttpStatusCode.CLASS_RETRIVED_SUCCESSFULLY.getMessage());
		getListofClasses.setResult(allClassResult);
		return getListofClasses;
	}

//	@Override
//	public ByteArrayInputStream load() {
//		List<ClassDetail> classd = classRepository.findAll();
//		ByteArrayInputStream out = ClassCsvHelper.classCSV(classd);
//		return out;
//	}
}

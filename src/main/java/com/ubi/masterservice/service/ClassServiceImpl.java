package com.ubi.masterservice.service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ubi.masterservice.dto.pagination.PaginationResponse;
import org.apache.kafka.clients.admin.NewTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.ubi.masterservice.dto.classDto.ClassDto;
import com.ubi.masterservice.dto.classDto.ClassStudentDto;
import com.ubi.masterservice.dto.classDto.TeacherDto;
import com.ubi.masterservice.dto.response.Response;
import com.ubi.masterservice.dto.studentDto.StudentDto;
import com.ubi.masterservice.dto.user.UserDto;
import com.ubi.masterservice.entity.ClassDetail;
import com.ubi.masterservice.entity.School;
import com.ubi.masterservice.entity.Student;
import com.ubi.masterservice.error.CustomException;
import com.ubi.masterservice.error.HttpStatusCode;
import com.ubi.masterservice.error.Result;
import com.ubi.masterservice.externalServices.UserFeignService;
import com.ubi.masterservice.mapper.ClassMapper;
import com.ubi.masterservice.mapper.SchoolMapper;
import com.ubi.masterservice.mapper.StudentMapper;
import com.ubi.masterservice.repository.ClassRepository;
import com.ubi.masterservice.repository.SchoolRepository;
import com.ubi.masterservice.repository.StudentRepository;
import com.ubi.masterservice.util.PermissionUtil;

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
	private PermissionUtil permissionUtil;
	
	@Autowired
	private UserFeignService userFeignService;
	
	@Autowired
	private SchoolMapper schoolMapper;

	@Autowired
	private StudentMapper studentMapper;

	private String topicName="master_topic_add";

	private String topicDelete="master_delete";

	private String topicUpdateName="master_topic_update";

	private NewTopic topic;

	@Autowired
	private KafkaTemplate<String, String> kafkaTemplate;

	public ClassServiceImpl(NewTopic topic , KafkaTemplate<String, String> kafkaTemplate)
	{
		this.topic=topic;
		this.kafkaTemplate=kafkaTemplate;
	}


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
		classDetail.setTeacherId(classDto.getTeacherId());
		classDetail.setStudents(new HashSet<>());

		TeacherDto teacherDto = null;


		if (classDetail.getTeacherId() != null) {
			ClassDetail classDetail1 = classRepository.findByTeacherId(classDetail.getTeacherId());
			if(classDetail1 != null){
				throw new CustomException(HttpStatusCode.BAD_REQUEST_EXCEPTION.getCode(),
						HttpStatusCode.BAD_REQUEST_EXCEPTION,
						"Given teacher is already mapped with another class",
						res);
			}

			String currJwtToken = "Bearer " + permissionUtil.getCurrentUsersToken();
			ResponseEntity<Response<UserDto>> teacherResponse = userFeignService.getTeacherById(currJwtToken,
					classDetail.getTeacherId().toString());
			UserDto userDto = teacherResponse.getBody().getResult().getData();
			if (userDto != null) {
				teacherDto = new TeacherDto(userDto.getId(), userDto.getContactInfoDto().getFirstName(),
						userDto.getContactInfoDto().getLastName());
			}
		}
		//classDetail.setTeacherId(classDto.getTeacherId());
		
		
		ClassDetail savedClass=classRepository.save(classDetail);
		ClassStudentDto classStudentDto=classMapper.toStudentDto(savedClass);
		classStudentDto.setTeacherDto(teacherDto);
		res.setData(classStudentDto);
		response.setStatusCode(HttpStatusCode.RESOURCE_CREATED_SUCCESSFULLY.getCode());
		response.setMessage(HttpStatusCode.RESOURCE_CREATED_SUCCESSFULLY.getMessage());
		response.setResult(res);

		ObjectMapper obj = new ObjectMapper();

		String jsonStr = null;
		try {
			jsonStr = obj.writeValueAsString(res.getData());
			LOGGER.info(jsonStr);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		kafkaTemplate.send(topicName,0,"Key1",jsonStr);
		LOGGER.info(String.format("Order Event => %s", jsonStr.toString()));

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

			String currJwtToken = "Bearer " + permissionUtil.getCurrentUsersToken();

			TeacherDto teacherDto = null;

			if (classDetail.getTeacherId() != null) {
				ResponseEntity<Response<UserDto>> teacherResponse = userFeignService.getTeacherById(currJwtToken,
						classDetail.getTeacherId().toString());
				UserDto userDto = teacherResponse.getBody().getResult().getData();
				if (userDto != null) {
					teacherDto = new TeacherDto(userDto.getId(), userDto.getContactInfoDto().getFirstName(),
							userDto.getContactInfoDto().getLastName());
				}
			}
			
			
			//Set<Student> s1=classDetail.getStudents();
			Set<StudentDto> studentDto=classDetail.getStudents().stream()
					.map(students -> studentMapper.entityToDto(students)).collect(Collectors.toSet());
			classStudentDto.setStudentDto(studentDto);
			classStudentDto.setTeacherDto(teacherDto);
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

		String currJwtToken = "Bearer " + permissionUtil.getCurrentUsersToken();

		TeacherDto teacherDto = null;

		if (classDetail.get().getTeacherId() != null) {
			ResponseEntity<Response<UserDto>> teacherResponse = userFeignService.getTeacherById(currJwtToken,
					classDetail.get().getTeacherId().toString());
			UserDto userDto = teacherResponse.getBody().getResult().getData();
			if (userDto != null) {
				teacherDto = new TeacherDto(userDto.getId(), userDto.getContactInfoDto().getFirstName(),
						userDto.getContactInfoDto().getLastName());
			}
		}
		
		
		ClassStudentDto classStudentDto=new ClassStudentDto();
		classStudentDto.setClassDto(classMapper.entityToDto(classDetail.get()));
		classStudentDto.setSchoolDto(schoolMapper.entityToDto(classDetail.get().getSchool()));
		Set<StudentDto> studentDto=classDetail.get().getStudents().stream()
				.map(students -> studentMapper.entityToDto(students)).collect(Collectors.toSet());
		classStudentDto.setTeacherDto(teacherDto);
		classStudentDto.setStudentDto(studentDto);
		
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
		res.setData(classMapper.entityToDto(classes.get()));
		response.setMessage(HttpStatusCode.CLASS_DELETED_SUCCESSFULLY.getMessage());
		response.setStatusCode(HttpStatusCode.CLASS_DELETED_SUCCESSFULLY.getCode());
		response.setResult(res);

		ObjectMapper obj = new ObjectMapper();

		String jsonStr = null;
		try {
			jsonStr = obj.writeValueAsString(res.getData());
			LOGGER.info(jsonStr);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		kafkaTemplate.send(topicDelete,0,"Key3",jsonStr);
		LOGGER.info(String.format("Order Event => %s", jsonStr.toString()));


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
		existingClassDetail.setClassName(classDetailDto.getClassName());
		existingClassDetail.setClassCode(classDetailDto.getClassCode());
		existingClassDetail.setSchoolId(classDetailDto.getSchoolId());
		existingClassDetail.setTeacherId(classDetailDto.getTeacherId());

		String currJwtToken = "Bearer " + permissionUtil.getCurrentUsersToken();

		TeacherDto teacherDto = null;

		if (existingClassDetail.getTeacherId() != null) {
			ResponseEntity<Response<UserDto>> teacherResponse = userFeignService.getTeacherById(currJwtToken,
					existingClassDetail.getTeacherId().toString());
			UserDto userDto = teacherResponse.getBody().getResult().getData();
			if (userDto != null) {
				teacherDto = new TeacherDto(userDto.getId(), userDto.getContactInfoDto().getFirstName(),
						userDto.getContactInfoDto().getLastName());
			}
		}
		existingClassDetail.setTeacherId(classDetailDto.getTeacherId());
		
		ClassDetail classDetail1=classMapper.dtoToEntity(existingClassDetail);
		ClassDetail updatedClassDetail=classRepository.save(classDetail1);
		ClassStudentDto classStudentDto=classMapper.toStudentDto(updatedClassDetail);
		classStudentDto.setTeacherDto(teacherDto);
		res.setData(classStudentDto);
		response.setMessage(HttpStatusCode.CLASS_UPDATED.getMessage());
		response.setStatusCode(HttpStatusCode.CLASS_UPDATED.getCode());
		response.setResult(res);


		ObjectMapper obj = new ObjectMapper();

		String jsonStr = null;
		try {
			jsonStr = obj.writeValueAsString(res.getData());
			LOGGER.info(jsonStr);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		kafkaTemplate.send(topicUpdateName,0, "Key2",jsonStr);
		LOGGER.info(String.format("Order Event => %s", jsonStr.toString()));
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
		
		String currJwtToken = "Bearer " + permissionUtil.getCurrentUsersToken();

		TeacherDto teacherDto = null;

		if (classDetail.getTeacherId() != null) {
			ResponseEntity<Response<UserDto>> teacherResponse = userFeignService.getTeacherById(currJwtToken,
					classDetail.getTeacherId().toString());
			UserDto userDto = teacherResponse.getBody().getResult().getData();
			if (userDto != null) {
				teacherDto = new TeacherDto(userDto.getId(), userDto.getContactInfoDto().getFirstName(),
						userDto.getContactInfoDto().getLastName());
			}
		}
		
		
		ClassStudentDto classStudentDto=new ClassStudentDto();
		classStudentDto.setClassDto(classMapper.entityToDto(classDetail));
		classStudentDto.setSchoolDto(schoolMapper.entityToDto(classDetail.getSchool()));
		Set<StudentDto> studentDto=classDetail.getStudents().stream()
				.map(students -> studentMapper.entityToDto(students)).collect(Collectors.toSet());
		classStudentDto.setStudentDto(studentDto);
		classStudentDto.setTeacherDto(teacherDto);
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

	@Override
	public Response<ClassDto> getClassByTeacherId(Long teacherId) {
		ClassDetail classDetail = classRepository.findByTeacherId(teacherId);
		if(classDetail == null){
			throw new CustomException(HttpStatusCode.NO_CONTENT.getCode(),
					HttpStatusCode.NO_CONTENT,
					"Class Not Found For Given Teacher Id", null);
		}
		ClassDto classDto = classMapper.entityToDto(classDetail);
		return new Response<ClassDto>(new Result<>(classDto));
	}

}

package com.ubi.masterservice.service;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.kafka.clients.admin.NewTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ubi.masterservice.dto.pagination.PaginationResponse;
import com.ubi.masterservice.dto.response.Response;
import com.ubi.masterservice.dto.studentDto.StudentDetailsDto;
import com.ubi.masterservice.dto.studentDto.StudentDto;
import com.ubi.masterservice.dto.studentDto.StudentPromoteDemoteDto;
import com.ubi.masterservice.dto.studentDto.StudentVerifyDto;
import com.ubi.masterservice.entity.ClassDetail;
import com.ubi.masterservice.entity.EducationalInstitution;
import com.ubi.masterservice.entity.Region;
import com.ubi.masterservice.entity.Student;
import com.ubi.masterservice.entity.StudentPromoteDemote;
import com.ubi.masterservice.error.CustomException;
import com.ubi.masterservice.error.HttpStatusCode;
import com.ubi.masterservice.error.Result;
import com.ubi.masterservice.mapper.StudentMapper;
import com.ubi.masterservice.repository.ClassRepository;
import com.ubi.masterservice.repository.StudentPromoteDemoteRepository;
import com.ubi.masterservice.repository.StudentRepository;

@Service
public class StudentServiceImpl implements StudentService {

	private static  final Logger LOGGER = LoggerFactory.getLogger(SchoolServiceImpl.class);
	@Autowired
	private StudentMapper studentMapper;

	@Autowired
	ClassRepository classRepository;

	@Autowired
	private StudentRepository studentRepository;

	@Autowired
	private StudentPromoteDemoteRepository promoteDemoterepository;

	@Autowired
	Result result;

	private String topicName="master_topic_add";

	private String topicDelete="master_delete";

	private String topicUpdateName="master_topic_update";

	private String topicPartialUpdate="master_topic_student_patch";

	private String topicPromote="master_topic_promote";

	private String topicDemote="master_topic_demote";

	private String topicVerifyByTeacher="master_topic_verify_by_teacher";

	private String topicVerifyByPrincipal="master_topic_verify_by_principal";

	private NewTopic topic;

	@Autowired
	private KafkaTemplate<String, String> kafkaTemplate;

	public StudentServiceImpl(NewTopic topic , KafkaTemplate<String, String> kafkaTemplate)
	{
		this.topic=topic;
		this.kafkaTemplate=kafkaTemplate;
	}


	public Response<StudentDto> saveStudent(StudentDto studentDto) {
		Result<StudentDto> res = new Result<>();
		Response<StudentDto> response = new Response<>();
		
		 Student std = studentRepository
				.getStudentByRollNo(
						studentDto.getRollNo());
		 

		if (studentDto.getStudentFirstName().length() == 0 || studentDto.getStudentLastName().length() == 0) {
			throw new CustomException(HttpStatusCode.NO_STUDENT_NAME_FOUND.getCode(),
					HttpStatusCode.NO_STUDENT_NAME_FOUND, HttpStatusCode.NO_STUDENT_NAME_FOUND.getMessage(), res);
		}

		if (studentDto.getClassId()==null ) {
			throw new CustomException(HttpStatusCode.NO_CLASSID_FOUND.getCode(),
					HttpStatusCode.NO_CLASSID_FOUND, HttpStatusCode.NO_CLASSID_FOUND.getMessage(), res);
		}
		
		   if (std != null) {
			throw new CustomException(HttpStatusCode.ROLLNO_ALREADY_EXIST.getCode(),
					HttpStatusCode.ROLLNO_ALREADY_EXIST,
					HttpStatusCode.ROLLNO_ALREADY_EXIST.getMessage(), res);
		}
		 
		
		
		ClassDetail classDetail = classRepository.getReferenceById(studentDto.getClassId());

		Student student = studentMapper.dtoToEntity(studentDto);
		student.setClassDetail(classDetail);
		student.setVerifiedByPrincipal(false);
		student.setVerifiedByTeacher(false);
		student.setIsActivate(false);
		student.setIsPhysicallyHandicapped(false);

		Student savedStudent = studentRepository.save(student);
		res.setData(studentMapper.entityToDto(savedStudent));
		//LOGGER.info("i am working -> " ,studentMapper.entityToDto(savedStudent).toString());
		response.setStatusCode(HttpStatusCode.RESOURCE_CREATED_SUCCESSFULLY.getCode());
		response.setMessage(HttpStatusCode.RESOURCE_CREATED_SUCCESSFULLY.getMessage());
		response.setResult(res);
		ObjectMapper obj = new ObjectMapper();

		String jsonStr = null;
		try {
			//Date date=(Date) res.getData().getDateOfBirth().toString();
			jsonStr = obj.writeValueAsString(res.getData());
//			LOGGER.info(String.format("Heloo-------",jsonStr));
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		kafkaTemplate.send(topicName,4,"Key1",jsonStr);
		LOGGER.info(String.format("Hello-------"));
		LOGGER.info(String.format("Order Event => %s", jsonStr.toString()));
		return response;
	}
	
	public Response<PaginationResponse<List<StudentDetailsDto>>> getStudents(String fieldName,String searchByField,Integer PageNumber, Integer PageSize) throws ParseException {
		Result<PaginationResponse<List<StudentDetailsDto>>> res = new Result<>();
		Pageable paging = PageRequest.of(PageNumber, PageSize);
		Response<PaginationResponse<List<StudentDetailsDto>>> getListofStudent = new Response<>();
		Page<Student> list = this.studentRepository.findAll(paging);
		List<StudentDetailsDto> studentDtos;
		PaginationResponse<List<StudentDetailsDto>> paginationResponse = null;
		Page<Student> studentData = null;
		String strDateRegEx ="^((?:19|20)[0-9][0-9])-(0?[1-9]|1[012])-(0?[1-9]|[12][0-9]|3[01])$";
		if(!fieldName.equals("*") && !searchByField.equals("*"))
		{
			if(searchByField.matches(strDateRegEx)) {
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
				Date localDate = formatter.parse(searchByField);
				if(fieldName.equalsIgnoreCase("dateOfBirth")) {
					studentData = studentRepository.findByDateOfBirth(localDate,paging);
				} else {
					studentData = studentRepository.findByJoiningDate(localDate,paging);
				}
				if(studentData.getNumberOfElements() == 0) {
					getListofStudent.setStatusCode(HttpStatusCode.NO_CONTENT.getCode());
					getListofStudent.setMessage("No Student Found with given field");
					getListofStudent.setResult( new Result(null) );
					return getListofStudent;		
				}
				studentDtos = (studentData.toList().stream().map(student -> studentMapper.toStudentDetails(student)).collect(Collectors.toList()));
				paginationResponse=new PaginationResponse<List<StudentDetailsDto>>(studentDtos,studentData.getTotalPages(),studentData.getTotalElements());
			} else {
				if(fieldName.equalsIgnoreCase("studentFirstName")) {
					studentData = studentRepository.findByStudentFirstNameIgnoreCase(searchByField, paging);
				}
				if(fieldName.equalsIgnoreCase("studentLastName")) {
					studentData = studentRepository.findByStudentLastNameIgnoreCase(searchByField, paging);
				}
				if(fieldName.equalsIgnoreCase("category")) {
					studentData = studentRepository.findByCategoryIgnoreCase(searchByField, paging);
				}
				if(fieldName.equalsIgnoreCase("minority")) {
					studentData = studentRepository.findByMinorityIgnoreCase(searchByField, paging);
				}
				if(fieldName.equalsIgnoreCase("fatherName")) {
					studentData = studentRepository.findByFatherNameIgnoreCase(searchByField,paging);
				}
				if(fieldName.equalsIgnoreCase("fatherOccupation")) {
					studentData = studentRepository.findByFatherOccupationIgnoreCase(searchByField,paging);
				}
				if(fieldName.equalsIgnoreCase("motherName")) {
					studentData = studentRepository.findByMotherNameIgnoreCase(searchByField,paging);
				}
				if(fieldName.equalsIgnoreCase("gender")) {
					studentData = studentRepository.findByGenderIgnoreCase(searchByField,paging);
				}
				if(fieldName.equalsIgnoreCase("studentId")) {
					studentData = studentRepository.findByStudentId(Long.parseLong(searchByField),paging);
				}
				if(fieldName.equalsIgnoreCase("rollNo")) {
					studentData = studentRepository.findByRollNo(Long.parseLong(searchByField),paging);
				}
				if(fieldName.equalsIgnoreCase("isPhysicallyHandicapped")) {
					studentData = studentRepository.findByIsPhysicallyHandicapped(Boolean.parseBoolean(searchByField),paging);
				}
				if(fieldName.equalsIgnoreCase("lastVerifiedByTeacher")) {
					studentData = studentRepository.findByLastVerifiedByTeacher(Long.parseLong(searchByField),paging);
				}
				if(fieldName.equalsIgnoreCase("lastVerifiedByPrincipal")) {
					studentData = studentRepository.findByLastVerifiedByPrincipal(Long.parseLong(searchByField),paging);
				}
				if(fieldName.equalsIgnoreCase("verifiedByTeacher")) {
					studentData = studentRepository.findByVerifiedByTeacher(Boolean.parseBoolean(searchByField),paging);
				}
				if(fieldName.equalsIgnoreCase("currentStatus")) {
					studentData = studentRepository.findByCurrentStatus(searchByField,paging);
				}
				if(fieldName.equalsIgnoreCase("verifiedByPrincipal")) {
					studentData = studentRepository.findByVerifiedByPrincipal(Boolean.parseBoolean(searchByField),paging);
				}
				if(studentData.getNumberOfElements() == 0) {
					getListofStudent.setStatusCode(HttpStatusCode.NO_CONTENT.getCode());
					getListofStudent.setMessage("No Student Found with given field");
					getListofStudent.setResult( new Result(null) );
					return getListofStudent;
					
				} else {
				studentDtos = (studentData.toList().stream().map(student -> studentMapper.toStudentDetails(student)).collect(Collectors.toList()));
				paginationResponse=new PaginationResponse<List<StudentDetailsDto>>(studentDtos,studentData.getTotalPages(),studentData.getTotalElements());
				}
			}
		} else {
			studentDtos = (list.toList().stream().map(student -> studentMapper.toStudentDetails(student)).collect(Collectors.toList()));
			paginationResponse=new PaginationResponse<List<StudentDetailsDto>>(studentDtos,list.getTotalPages(),list.getTotalElements());
		}
		if (list.isEmpty()) {
			getListofStudent.setStatusCode(HttpStatusCode.NO_CONTENT.getCode());
			getListofStudent.setMessage("No Student Found");
			getListofStudent.setResult( new Result(null) );
			return getListofStudent;
			
		}
		res.setData(paginationResponse);
		getListofStudent.setStatusCode(HttpStatusCode.SUCCESSFUL.getCode());
		getListofStudent.setMessage("Student retrived");
		
		getListofStudent.setResult(res);
		return getListofStudent;
	}

	public Response<StudentDetailsDto> getStudentById(Long id) {
		Result<StudentDetailsDto> res = new Result<>();
		Response<StudentDetailsDto> getStudent = new Response<StudentDetailsDto>();
		Optional<Student> std = this.studentRepository.findById(id);
		Result<StudentDetailsDto> studentResult = new Result<>();
		if (!std.isPresent()) {
			throw new CustomException(HttpStatusCode.NO_STUDENT_MATCH_WITH_ID.getCode(),
					HttpStatusCode.NO_STUDENT_MATCH_WITH_ID, HttpStatusCode.NO_STUDENT_MATCH_WITH_ID.getMessage(), res);
		}
		StudentDetailsDto student = studentMapper.toStudentDetails(std.get());
		studentResult.setData(student);
		getStudent.setStatusCode(200);
		getStudent.setResult(studentResult);
		return getStudent;
	}

	@Override
	public Response<StudentDto> deleteById(Long id) {
		Result<StudentDto> res = new Result<>();
		Optional<Student> student = studentRepository.findById(id);

		if (!student.isPresent()) {
			throw new CustomException(HttpStatusCode.RESOURCE_NOT_FOUND.getCode(), HttpStatusCode.RESOURCE_NOT_FOUND,
					HttpStatusCode.RESOURCE_NOT_FOUND.getMessage(), res);
		}

		ClassDetail classDetail=student.get().getClassDetail();
		if(classDetail!=null)
		{
			classDetail.getStudents().remove(student.get());
		}
		studentRepository.deleteById(id);
		Response<StudentDto> response = new Response<>();
		res.setData(studentMapper.entityToDto(student.get()));
		response.setMessage(HttpStatusCode.STUDENT_DELETED.getMessage());
		response.setStatusCode(HttpStatusCode.STUDENT_DELETED.getCode());
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
		kafkaTemplate.send(topicDelete,4,"Key3",jsonStr);
		LOGGER.info(String.format("Order Event => %s", jsonStr.toString()));
		return response;
	}

	public Response<StudentDto> updateStudent(StudentDto studentDto) {
		Result<StudentDto> res = new Result<>();
		Optional<Student> existingStudentContainer = studentRepository.findById(studentDto.getStudentId());
		if (!existingStudentContainer.isPresent()) {
			throw new CustomException(HttpStatusCode.NO_STUDENT_FOUND.getCode(), HttpStatusCode.NO_STUDENT_FOUND,
					HttpStatusCode.NO_STUDENT_FOUND.getMessage(), res);
		}
		StudentDto existingStudent = studentMapper.entityToDto(existingStudentContainer.get());
		existingStudent.setStudentFirstName(studentDto.getStudentFirstName());
		existingStudent.setStudentLastName(studentDto.getStudentLastName());
//		existingStudent.setStudentStatus(studentDto.isStudentStatus());
		existingStudent.setCategory(studentDto.getCategory());
		existingStudent.setFatherName(studentDto.getFatherName());
		existingStudent.setFatherOccupation(studentDto.getFatherOccupation());
		existingStudent.setMotherName(studentDto.getMotherName());
		existingStudent.setMotherOccupation(studentDto.getMotherOccupation());
		existingStudent.setGender(studentDto.getGender());
		existingStudent.setJoiningDate(studentDto.getJoiningDate());
//		existingStudent.setStatus(studentDto.getStatus());
		existingStudent.setVerifiedByTeacher(studentDto.getVerifiedByTeacher());
		existingStudent.setVerifiedByPrincipal(studentDto.getVerifiedByPrincipal());
		existingStudent.setRollNo(studentDto.getRollNo());
		existingStudent.setIsPhysicallyHandicapped(studentDto.getIsPhysicallyHandicapped());
	
		existingStudent.setVerifiedByPrincipal(studentDto.getVerifiedByPrincipal());
		

		existingStudent.setClassId(studentDto.getClassId());
		ClassDetail classDetail = classRepository.getReferenceById(studentDto.getClassId());
		Student student = studentMapper.dtoToEntity(existingStudent);


		student.setClassDetail(classDetail);
		Student updateStudent = studentRepository.save(student);
		Response<StudentDto> response = new Response<>();
		res.setData(studentMapper.entityToDto(updateStudent));
		response.setMessage(HttpStatusCode.STUDENT_UPDATED.getMessage());
		response.setStatusCode(HttpStatusCode.STUDENT_UPDATED.getCode());
		response.setResult(res);
		ObjectMapper obj = new ObjectMapper();

		String jsonStr = null;
		try {
			jsonStr = obj.writeValueAsString(res.getData());
			System.out.println(jsonStr);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		kafkaTemplate.send(topicUpdateName,4, "Key2",jsonStr);
		LOGGER.info(String.format("Order Event => %s", jsonStr.toString()));
		return response;
	}

	@Override
	public Response<StudentDto> changeActiveStatusToTrue(Long id) {

		Result<StudentDto> res = new Result<>();
		Response<StudentDto> response = new Response<>();

		if (this.getStudentById(id).getResult().getData() == null) {
			throw new CustomException(HttpStatusCode.RESOURCE_NOT_FOUND.getCode(), HttpStatusCode.RESOURCE_NOT_FOUND,
					HttpStatusCode.RESOURCE_NOT_FOUND.getMessage(), res);
		}

		Student student = studentRepository.getReferenceById(id);
		student.setIsActivate(true);
		Student updateStudent = studentRepository.save(student);
		res.setData(studentMapper.entityToDto(updateStudent));
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
		kafkaTemplate.send(topicPartialUpdate,0,"Key4",jsonStr);
		LOGGER.info(String.format("Order Event => %s", jsonStr.toString()));
		return response;

	}

	@Override
	public Response<StudentDto> changeActiveStatusToFalse(Long id) {
		Result<StudentDto> res = new Result<>();
		Response<StudentDto> response = new Response<>();

		if (this.getStudentById(id).getResult().getData() == null) {
			throw new CustomException(HttpStatusCode.RESOURCE_NOT_FOUND.getCode(), HttpStatusCode.RESOURCE_NOT_FOUND,
					HttpStatusCode.RESOURCE_NOT_FOUND.getMessage(), res);
		}

		Student student = studentRepository.getReferenceById(id);
		student.setIsActivate(false);
		Student updateStudent = studentRepository.save(student);
		res.setData(studentMapper.entityToDto(updateStudent));
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
		kafkaTemplate.send(topicPartialUpdate,1,"Key5",jsonStr);
		LOGGER.info(String.format("Order Event => %s", jsonStr.toString()));
		return response;

	}


	@Override
	public Response<List<StudentDto>> findByGenderAndCategoryAndMinority(String gender, String category,
																		 String minority) {
		Result<StudentDto> res = new Result<>();
		List<Student> student = studentRepository.findByGenderAndCategoryAndMinority(gender, category, minority);
		if (student.size() == 0) {
			throw new CustomException(HttpStatusCode.NO_ENTRY_FOUND.getCode(), HttpStatusCode.NO_ENTRY_FOUND,
					HttpStatusCode.NO_ENTRY_FOUND.getMessage(), res);
		}
		Response<List<StudentDto>> getListofStudent = new Response<>();
		getListofStudent.setStatusCode(200);
		getListofStudent.setResult(new Result<>(studentMapper.entitiesToDtos(student)));
		return getListofStudent;
	}


	@Override
	public Response<List<StudentVerifyDto>> verifiedByTeacher(String userId,StudentVerifyDto studentVerifyDto) {

		Result<List<StudentVerifyDto>> res = new Result<>();
		Result<StudentVerifyDto> result=new Result<>();
		for(Long category: studentVerifyDto.getStudentId()){

			Optional<Student> existingStudentContainer = studentRepository.findById(category);
			if (existingStudentContainer.isPresent()) {
				if(!Boolean.TRUE.equals(existingStudentContainer.get().getVerifiedByTeacher())) {
					existingStudentContainer.get().setVerifiedByTeacher(true);
					existingStudentContainer.get().setLastVerifiedByTeacher(Long.parseLong(userId));
					studentRepository.save(existingStudentContainer.get());
				}

			} else {
				throw new CustomException(HttpStatusCode.NO_STUDENT_FOUND.getCode(), HttpStatusCode.NO_STUDENT_FOUND,
						HttpStatusCode.NO_STUDENT_FOUND.getMessage(), res);
			}
		}

		Response<List<StudentVerifyDto>> response = new Response<>();
		result.setData(studentMapper.entityToDtoId(studentVerifyDto));
		response.setStatusCode(HttpStatusCode.STUDENT_VERIFIED_SUCCESSFULLY.getCode());
		response.setMessage(HttpStatusCode.STUDENT_VERIFIED_SUCCESSFULLY.getMessage());
		response.setResult(new Result(studentMapper.entityToDtoId(studentVerifyDto)));
		ObjectMapper obj = new ObjectMapper();

		String jsonStr = null;
		try {
			jsonStr = obj.writeValueAsString(result.getData());
			LOGGER.info(jsonStr);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		kafkaTemplate.send(topicVerifyByTeacher,0,"Key10",jsonStr);
		LOGGER.info(String.format("Order Event => %s", jsonStr.toString()));
		return response;
	}


	@Override
	public Response<List<StudentVerifyDto>> verifiedByPrincipal(String userId,StudentVerifyDto studentVerifyDto) {

		Result<StudentVerifyDto> result=new Result<>();

		for(Long category: studentVerifyDto.getStudentId()) {

			Optional<Student> existingStudentContainer = studentRepository.findById(category);
			if (existingStudentContainer.isPresent()) {

				if(Boolean.TRUE.equals(existingStudentContainer.get().getVerifiedByTeacher())) {
					existingStudentContainer.get().setVerifiedByPrincipal(true);
					existingStudentContainer.get().setLastVerifiedByPrincipal(Long.parseLong(userId));
					studentRepository.save(existingStudentContainer.get());

				} else {
					studentVerifyDto.getStudentId().remove(category);
				}

			}
		}
		Response<List<StudentVerifyDto>> response = new Response<>();
		result.setData(studentMapper.entityToDtoId(studentVerifyDto));
		response.setStatusCode(HttpStatusCode.STUDENT_VERIFIED_SUCCESSFULLY.getCode());
		response.setMessage(HttpStatusCode.STUDENT_VERIFIED_SUCCESSFULLY.getMessage());
		response.setResult(new Result(studentMapper.entityToDtoId(studentVerifyDto)));
		ObjectMapper obj = new ObjectMapper();

		String jsonStr = null;
		try {
			jsonStr = obj.writeValueAsString(result.getData());
			LOGGER.info(jsonStr);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		kafkaTemplate.send(topicVerifyByPrincipal,0,"Key11",jsonStr);
		LOGGER.info(String.format("Order Event => %s", jsonStr.toString()));
		return response;
	}

	@Override
	public Response<StudentDto> changeCurrentStatusToPromoted(Long id) {

		Result<StudentDto> res = new Result<>();
		Response<StudentDto> response = new Response<>();

		if (this.getStudentById(id).getResult().getData() == null) {
			throw new CustomException(HttpStatusCode.RESOURCE_NOT_FOUND.getCode(), HttpStatusCode.RESOURCE_NOT_FOUND,
					HttpStatusCode.RESOURCE_NOT_FOUND.getMessage(), res);
		}

		Student student = studentRepository.getReferenceById(id);
		student.setCurrentStatus("Promoted");
		Student updateStudent = studentRepository.save(student);
		res.setData(studentMapper.entityToDto(student));
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
		kafkaTemplate.send(topicPartialUpdate,2,"Key6",jsonStr);
		LOGGER.info(String.format("Order Event => %s", jsonStr.toString()));
		return response;
	}

	@Override
	public Response<StudentDto> changeCurrentStatusToDemoted(Long id) {
		Result<StudentDto> res = new Result<>();
		Response<StudentDto> response = new Response<>();

		if (this.getStudentById(id).getResult().getData() == null) {
			throw new CustomException(HttpStatusCode.RESOURCE_NOT_FOUND.getCode(), HttpStatusCode.RESOURCE_NOT_FOUND,
					HttpStatusCode.RESOURCE_NOT_FOUND.getMessage(), res);
		}

		Student student = studentRepository.getReferenceById(id);
		student.setCurrentStatus("Demoted");
		Student updateStudent = studentRepository.save(student);
		res.setData(studentMapper.entityToDto(student));
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
		kafkaTemplate.send(topicPartialUpdate,3,"Key7",jsonStr);
		LOGGER.info(String.format("Order Event => %s", jsonStr.toString()));
		return response;
	}


	@Override
	public Response<StudentPromoteDemoteDto> studentPromoted(String userId,StudentPromoteDemoteDto studentPromoteDemoteCreationDto) {
		Result<StudentPromoteDemoteDto> res = new Result<>();
		ClassDetail classDetails=classRepository.getReferenceById(studentPromoteDemoteCreationDto.getClassId());

		for(Long category: studentPromoteDemoteCreationDto.getStudentId()){

			Optional<Student> existingStudentContainer = studentRepository.findById(category);
			if (existingStudentContainer.isPresent()) {
				if(!Boolean.TRUE.equals(existingStudentContainer.get().getCurrentStatus())) {
					existingStudentContainer.get().setCurrentStatus("promoted");
					studentRepository.save(existingStudentContainer.get());
				}
				StudentPromoteDemote student =new StudentPromoteDemote();
				student.setClassId(studentPromoteDemoteCreationDto.getClassId());
				student.setStudentId(category);
				student.setPromoted(true);
				student.setUserId(Long.parseLong(userId));
				StudentPromoteDemote savedStudent = promoteDemoterepository.save(student);

				Student updatedStudent=existingStudentContainer.get();
				updatedStudent.setClassDetail(classDetails);
				updatedStudent.setCurrentStatus("promoted");
				studentRepository.save(updatedStudent);

			} else {
				throw new CustomException(HttpStatusCode.NO_STUDENT_FOUND.getCode(), HttpStatusCode.NO_STUDENT_FOUND,
						HttpStatusCode.NO_STUDENT_FOUND.getMessage(), res);
			}
		}


		Response<StudentPromoteDemoteDto> response = new Response<>();
		res.setData(studentMapper.entityToDtoId(studentPromoteDemoteCreationDto));
		response.setStatusCode(HttpStatusCode.STUDENT_PROMOTED_SUCCESSFULLY.getCode());
		response.setMessage(HttpStatusCode.STUDENT_PROMOTED_SUCCESSFULLY.getMessage());
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
		kafkaTemplate.send(topicPromote,0,"Key8",jsonStr);
		LOGGER.info(String.format("Order Event => %s", jsonStr.toString()));
		return response;
	}


	@Override
	public Response<StudentPromoteDemoteDto> studentDemoted(String userId, StudentPromoteDemoteDto studentPromoteDemoteCreationDto) {

		Result<StudentPromoteDemoteDto> res = new Result<>();
		ClassDetail classDetails=classRepository.getReferenceById(studentPromoteDemoteCreationDto.getClassId());


		for(Long category: studentPromoteDemoteCreationDto.getStudentId()){

			Optional<Student> existingStudentContainer = studentRepository.findById(category);
			if (existingStudentContainer.isPresent()) {
				if(!Boolean.TRUE.equals(existingStudentContainer.get().getCurrentStatus())) {
					existingStudentContainer.get().setCurrentStatus("demoted");
					studentRepository.save(existingStudentContainer.get());
				}
				StudentPromoteDemote student =new StudentPromoteDemote();
				student.setClassId(studentPromoteDemoteCreationDto.getClassId());
				student.setStudentId(category);
				student.setPromoted(false);
				student.setUserId(Long.parseLong(userId));
				StudentPromoteDemote savedStudent = promoteDemoterepository.save(student);

				Student updatedStudent=existingStudentContainer.get();
				updatedStudent.setClassDetail(classDetails);
				updatedStudent.setCurrentStatus("demoted");
				studentRepository.save(updatedStudent);

			}else {
				throw new CustomException(HttpStatusCode.NO_STUDENT_FOUND.getCode(), HttpStatusCode.NO_STUDENT_FOUND,
						HttpStatusCode.NO_STUDENT_FOUND.getMessage(), res);
			}
		}

		Response<StudentPromoteDemoteDto> response = new Response<>();

		res.setData(studentMapper.entityToDtoId(studentPromoteDemoteCreationDto));
		response.setStatusCode(HttpStatusCode.STUDENT_DEMOTED_SUCCESSFULLY.getCode());
		response.setMessage(HttpStatusCode.STUDENT_DEMOTED_SUCCESSFULLY.getMessage());
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
		kafkaTemplate.send(topicDemote,0,"Key9",jsonStr);
		LOGGER.info(String.format("Order Event => %s", jsonStr.toString()));
		return response;

	}
}

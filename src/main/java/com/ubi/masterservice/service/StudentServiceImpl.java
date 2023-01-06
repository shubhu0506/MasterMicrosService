package com.ubi.masterservice.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ubi.masterservice.dto.pagination.PaginationResponse;
import com.ubi.masterservice.dto.studentDto.StudentPromoteDemoteDto;
import com.ubi.masterservice.dto.studentDto.StudentVerifyDto;
import com.ubi.masterservice.entity.StudentPromoteDemote;
import com.ubi.masterservice.repository.StudentPromoteDemoteRepository;
import org.apache.kafka.clients.admin.NewTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.ubi.masterservice.dto.response.Response;
import com.ubi.masterservice.dto.studentDto.StudentDto;
import com.ubi.masterservice.entity.ClassDetail;
import com.ubi.masterservice.entity.Student;
import com.ubi.masterservice.error.CustomException;
import com.ubi.masterservice.error.HttpStatusCode;
import com.ubi.masterservice.error.Result;
import com.ubi.masterservice.mapper.StudentMapper;
import com.ubi.masterservice.repository.ClassRepository;
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

	private String topicName="master_topic_4";

	private String topicDelete="master_delete_topic";

	private NewTopic topic;

	@Autowired
	private KafkaTemplate<String, String> kafkaTemplate;

	public StudentServiceImpl(NewTopic topic , KafkaTemplate<String, String> kafkaTemplate)
	{
		this.topic=topic;
		this.kafkaTemplate=kafkaTemplate;
	}



//	public ByteArrayInputStream load() {
//		List<Student> student = studentRepository.findAll();
//
//		ByteArrayInputStream in = StudentCSVHelper.StudentToCSV(student);
//		return in;
//	}

	public Response<StudentDto> saveStudent(StudentDto studentDto) {
		Result<StudentDto> res = new Result<>();
		Response<StudentDto> response = new Response<>();


		if (studentDto.getStudentName().isEmpty() || studentDto.getStudentName().length() == 0) {
			throw new CustomException(HttpStatusCode.NO_STUDENT_NAME_FOUND.getCode(),
					HttpStatusCode.NO_STUDENT_NAME_FOUND, HttpStatusCode.NO_STUDENT_NAME_FOUND.getMessage(), res);
		}

		if (studentDto.getClassId()==null ) {
			throw new CustomException(HttpStatusCode.NO_CLASSID_FOUND.getCode(),
					HttpStatusCode.NO_CLASSID_FOUND, HttpStatusCode.NO_CLASSID_FOUND.getMessage(), res);
		}
		ClassDetail classDetail = classRepository.getReferenceById(studentDto.getClassId());

		Student student = studentMapper.dtoToEntity(studentDto);
		student.setClassDetail(classDetail);
		student.setVerifiedByPrincipal(false);
		student.setVerifiedByTeacher(false);
		student.setIsActivate(false);

		Student savedStudent = studentRepository.save(student);
		res.setData(studentMapper.entityToDto(savedStudent));
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
		kafkaTemplate.send(topicName,4,"Key1",jsonStr);
		LOGGER.info(String.format("Order Event => %s", jsonStr.toString()));
		return response;
	}

	public Response<PaginationResponse<List<StudentDto>>> getStudents(Integer PageNumber, Integer PageSize) {
		Result<PaginationResponse<List<StudentDto>>> res = new Result<>();
		Pageable paging = PageRequest.of(PageNumber, PageSize);
		Response<PaginationResponse<List<StudentDto>>> getListofStudent = new Response<>();
		Page<Student> list = this.studentRepository.findAll(paging);

		List<StudentDto> studentDtos = studentMapper.entitiesToDtos(list.toList());


		if (list.isEmpty()) {
			throw new CustomException(HttpStatusCode.NO_ENTRY_FOUND.getCode(), HttpStatusCode.NO_ENTRY_FOUND,
					HttpStatusCode.NO_ENTRY_FOUND.getMessage(), res);
		}

		PaginationResponse paginationResponse=new PaginationResponse<List<StudentDto>>(studentDtos,list.getTotalPages(),list.getTotalElements());

		res.setData(paginationResponse);
		getListofStudent.setStatusCode(200);
		getListofStudent.setResult(res);
		return getListofStudent;
	}

	public Response<StudentDto> getStudentById(Long id) {
		Result<StudentDto> res = new Result<>();
		Response<StudentDto> getStudent = new Response<StudentDto>();
		Optional<Student> std = this.studentRepository.findById(id);
		Result<StudentDto> studentResult = new Result<>();
		if (!std.isPresent()) {
			throw new CustomException(HttpStatusCode.NO_STUDENT_MATCH_WITH_ID.getCode(),
					HttpStatusCode.NO_STUDENT_MATCH_WITH_ID, HttpStatusCode.NO_STUDENT_MATCH_WITH_ID.getMessage(), res);
		}
		StudentDto student = studentMapper.entityToDto(std.get());
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

		response.setMessage(HttpStatusCode.STUDENT_DELETED.getMessage());
		response.setStatusCode(HttpStatusCode.STUDENT_DELETED.getCode());
		response.setResult(new Result<StudentDto>(studentMapper.entityToDto(student.get())));
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
		existingStudent.setStudentName(studentDto.getStudentName());
		existingStudent.setStudentStatus(studentDto.isStudentStatus());
		existingStudent.setCategory(studentDto.getCategory());
		existingStudent.setFatherName(studentDto.getFatherName());
		existingStudent.setFatherOccupation(studentDto.getFatherOccupation());
		existingStudent.setMotherName(studentDto.getMotherName());
		existingStudent.setMotherOccupation(studentDto.getMotherOccupation());
		existingStudent.setGender(studentDto.getGender());
		existingStudent.setJoiningDate(studentDto.getJoiningDate());
		existingStudent.setStatus(studentDto.getStatus());
		existingStudent.setVerifiedByTeacher(studentDto.isVerifiedByTeacher());
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
		kafkaTemplate.send(topicName,4, "Key2",jsonStr);
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
		response.setStatusCode(HttpStatusCode.RESOURCE_CREATED_SUCCESSFULLY.getCode());
		response.setMessage(HttpStatusCode.RESOURCE_CREATED_SUCCESSFULLY.getMessage());
		response.setResult(new Result<StudentDto>(studentMapper.entityToDto(updateStudent)));
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
		response.setStatusCode(HttpStatusCode.RESOURCE_CREATED_SUCCESSFULLY.getCode());
		response.setMessage(HttpStatusCode.RESOURCE_CREATED_SUCCESSFULLY.getMessage());
		response.setResult(new Result<StudentDto>(studentMapper.entityToDto(updateStudent)));
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
		for(Long category: studentVerifyDto.getStudentId()){

			Optional<Student> existingStudentContainer = studentRepository.findById(category);
			if (existingStudentContainer.isPresent()) {
				if(!Boolean.TRUE.equals(existingStudentContainer.get().getVerifiedByTeacher())) {
					existingStudentContainer.get().setVerifiedByTeacher(true);
					studentRepository.save(existingStudentContainer.get());
				}

			} else {
				throw new CustomException(HttpStatusCode.NO_STUDENT_FOUND.getCode(), HttpStatusCode.NO_STUDENT_FOUND,
						HttpStatusCode.NO_STUDENT_FOUND.getMessage(), res);
			}
		}

		Response<List<StudentVerifyDto>> response = new Response<>();
		response.setStatusCode(HttpStatusCode.STUDENT_VERIFIED_SUCCESSFULLY.getCode());
		response.setMessage(HttpStatusCode.STUDENT_VERIFIED_SUCCESSFULLY.getMessage());
		response.setResult(new Result(studentMapper.entityToDtoId(studentVerifyDto)));
		return response;
	}


	@Override
	public Response<List<StudentVerifyDto>> verifiedByPrincipal(String userId,StudentVerifyDto studentVerifyDto) {

		for(Long category: studentVerifyDto.getStudentId()) {

			Optional<Student> existingStudentContainer = studentRepository.findById(category);
			if (existingStudentContainer.isPresent()) {

				if(Boolean.TRUE.equals(existingStudentContainer.get().getVerifiedByTeacher())) {
					existingStudentContainer.get().setVerifiedByPrincipal(true);
					studentRepository.save(existingStudentContainer.get());

				} else {
					studentVerifyDto.getStudentId().remove(category);
				}

			}
		}
		Response<List<StudentVerifyDto>> response = new Response<>();
		response.setStatusCode(HttpStatusCode.STUDENT_VERIFIED_SUCCESSFULLY.getCode());
		response.setMessage(HttpStatusCode.STUDENT_VERIFIED_SUCCESSFULLY.getMessage());
		response.setResult(new Result(studentMapper.entityToDtoId(studentVerifyDto)));
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
		response.setStatusCode(HttpStatusCode.RESOURCE_CREATED_SUCCESSFULLY.getCode());
		response.setMessage(HttpStatusCode.RESOURCE_CREATED_SUCCESSFULLY.getMessage());
		response.setResult(new Result<StudentDto>(studentMapper.entityToDto(student)));

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
		response.setStatusCode(HttpStatusCode.RESOURCE_CREATED_SUCCESSFULLY.getCode());
		response.setMessage(HttpStatusCode.RESOURCE_CREATED_SUCCESSFULLY.getMessage());
		response.setResult(new Result<StudentDto>(studentMapper.entityToDto(student)));

		return response;
	}


	@Override
	public Response<StudentPromoteDemoteDto> studentPromoted(String userId,StudentPromoteDemoteDto studentPromoteDemoteCreationDto) {
		Result<List<StudentPromoteDemoteDto>> res = new Result<>();
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
		response.setStatusCode(HttpStatusCode.STUDENT_PROMOTED_SUCCESSFULLY.getCode());
		response.setMessage(HttpStatusCode.STUDENT_PROMOTED_SUCCESSFULLY.getMessage());
		response.setResult(new Result(studentMapper.entityToDtoId(studentPromoteDemoteCreationDto)));
		return response;
	}






	@Override
	public Response<StudentPromoteDemoteDto> studentDemoted(String userId, StudentPromoteDemoteDto studentPromoteDemoteCreationDto) {

		Result<List<StudentPromoteDemoteDto>> res = new Result<>();
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
		response.setStatusCode(HttpStatusCode.STUDENT_DEMOTED_SUCCESSFULLY.getCode());
		response.setMessage(HttpStatusCode.STUDENT_DEMOTED_SUCCESSFULLY.getMessage());
		response.setResult(new Result(studentMapper.entityToDtoId(studentPromoteDemoteCreationDto)));
		return response;

	}
}

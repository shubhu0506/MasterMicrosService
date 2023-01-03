package com.ubi.masterservice.service;

import java.io.ByteArrayInputStream;
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

import com.ubi.masterservice.dto.educationalInstitutiondto.EducationRegionGetDto;
import com.ubi.masterservice.dto.educationalInstitutiondto.EducationalInstitutionDto;
import com.ubi.masterservice.dto.educationalInstitutiondto.EducationalRegionDto;
import com.ubi.masterservice.dto.regionDto.RegionGet;
import com.ubi.masterservice.dto.response.Response;
import com.ubi.masterservice.entity.EducationalInstitution;
import com.ubi.masterservice.entity.Region;
import com.ubi.masterservice.error.CustomException;
import com.ubi.masterservice.error.HttpStatusCode;
import com.ubi.masterservice.error.Result;
import com.ubi.masterservice.mapper.EducationalInstitutionMapper;
import com.ubi.masterservice.mapper.RegionMapper;
import com.ubi.masterservice.repository.EducationalInstitutionRepository;
import com.ubi.masterservice.repository.RegionRepository;

@Service
public class EducationalInstitutionServiceImpl implements EducationalInstitutionService {

	@Autowired
	private EducationalInstitutionRepository educationalInstitutionRepository;

	@Autowired
	private EducationalInstitutionMapper educationalInstitutionMapper;

	@Autowired
	private RegionMapper regionMapper;

	@Autowired
	private RegionRepository regionRepository;

	Logger logger = LoggerFactory.getLogger(EducationalInstitutionServiceImpl.class);

	@Override
	public Response<EducationalRegionDto> addEducationalInstitution(
			EducationalInstitutionDto educationalInstitutionDto) {

		Result<EducationalRegionDto> res = new Result<>();

		Response<EducationalRegionDto> response = new Response<>();
		Optional<EducationalInstitution> tempeducationalInstitution = educationalInstitutionRepository
				.findById(educationalInstitutionDto.getId());

		EducationalInstitution educationalInstitutionName = educationalInstitutionRepository
				.getEducationalInstitutionByeducationalInstitutionName(
						educationalInstitutionDto.getEducationalInstitutionName());

		EducationalInstitution educationalInstitutionCode = educationalInstitutionRepository
				.getEducationalInstitutionByeducationalInstitutionCode(
						educationalInstitutionDto.getEducationalInstitutionCode());

		if (tempeducationalInstitution.isPresent()) {
			throw new CustomException(HttpStatusCode.NO_EDUCATIONAL_INSTITUTION_FOUND.getCode(),
					HttpStatusCode.NO_EDUCATIONAL_INSTITUTION_FOUND,
					HttpStatusCode.NO_EDUCATIONAL_INSTITUTION_FOUND.getMessage(), res);
		}

		if (educationalInstitutionName != null) {
			throw new CustomException(HttpStatusCode.EDUCATIONAL_INSTITUTION_NAME_ALREADY_EXISTS.getCode(),
					HttpStatusCode.EDUCATIONAL_INSTITUTION_NAME_ALREADY_EXISTS,
					HttpStatusCode.EDUCATIONAL_INSTITUTION_NAME_ALREADY_EXISTS.getMessage(), res);
		}

		if (educationalInstitutionCode != null) {
			throw new CustomException(HttpStatusCode.EDUCATIONAL_INSTITUTION_CODE_ALREADY_EXISTS.getCode(),
					HttpStatusCode.EDUCATIONAL_INSTITUTION_CODE_ALREADY_EXISTS,
					HttpStatusCode.EDUCATIONAL_INSTITUTION_CODE_ALREADY_EXISTS.getMessage(), res);
		}

		EducationalInstitution educationalInstitution = new EducationalInstitution();
		educationalInstitution.setId(educationalInstitutionDto.getId());
		educationalInstitution.setEducationalInstitutionCode(educationalInstitutionDto.getEducationalInstitutionCode());
		educationalInstitution.setEducationalInstitutionName(educationalInstitutionDto.getEducationalInstitutionName());
		educationalInstitution.setEducationalInstitutionType(educationalInstitutionDto.getEducationalInstitutionType());
		educationalInstitution.setState(educationalInstitutionDto.getState());
		educationalInstitution.setExemptionFlag(educationalInstitutionDto.getExemptionFlag());
		educationalInstitution.setStrength(educationalInstitutionDto.getStrength());
		educationalInstitution.setVvnAccount(educationalInstitutionDto.getVvnAccount());
		educationalInstitution.setRegion(new HashSet<>());

		for (Integer regionId : educationalInstitutionDto.getRegionId()) {
			Region region = regionRepository.getReferenceById(regionId);
			System.out.println("region --- " + region.toString());
			if (region != null)
				educationalInstitution.getRegion().add(region);

		}

		if (educationalInstitution.getRegion().isEmpty()) {
			throw new CustomException(HttpStatusCode.NO_REGION_ADDED.getCode(), HttpStatusCode.NO_REGION_ADDED,
					HttpStatusCode.NO_REGION_ADDED.getMessage(), res);
		}

		EducationalInstitution savedEducationalInstitution = educationalInstitutionRepository
				.save(educationalInstitution);

		EducationalRegionDto educationalRegionDto = educationalInstitutionMapper
				.toEducationalRegionDto(savedEducationalInstitution);

		response.setStatusCode(HttpStatusCode.RESOURCE_CREATED_SUCCESSFULLY.getCode());
		response.setMessage(HttpStatusCode.RESOURCE_CREATED_SUCCESSFULLY.getMessage());
		response.setResult(new Result<EducationalRegionDto>(educationalRegionDto));
		return response;

	}

	@Override
	public Response<EducationRegionGetDto> getEducationalInstituteByName(String educationalInstitutionName) {

		Result<EducationRegionGetDto> res = new Result<>();
		res.setData(null);
		Response<EducationRegionGetDto> getEducationalInstitutionName = new Response<>();
		Optional<EducationalInstitution> educationalInst = this.educationalInstitutionRepository
				.findByeducationalInstitutionName(educationalInstitutionName);
		Result<EducationRegionGetDto> educationalInstitutionResult = new Result<>();
		if (!educationalInst.isPresent()) {
			throw new CustomException(HttpStatusCode.NO_EDUCATIONAL_INSTITUTION_NAME_FOUND.getCode(),
					HttpStatusCode.NO_EDUCATIONAL_INSTITUTION_NAME_FOUND,
					HttpStatusCode.NO_EDUCATIONAL_INSTITUTION_NAME_FOUND.getMessage(), res);
		}

		EducationRegionGetDto educationalRegionDto = new EducationRegionGetDto();
		educationalRegionDto
				.setEducationalInstituteDto(educationalInstitutionMapper.entityToDtos(educationalInst.get()));
		educationalRegionDto.setRegionDto(regionMapper.entitiesToDtos(educationalInst.get().getRegion()));

		res.setData(educationalRegionDto);

		getEducationalInstitutionName
				.setStatusCode(HttpStatusCode.EDUCATIONAL_INSTITUTION_RETRIVED_SUCCESSFULLY.getCode());
		getEducationalInstitutionName
				.setMessage(HttpStatusCode.EDUCATIONAL_INSTITUTION_RETRIVED_SUCCESSFULLY.getMessage());
		getEducationalInstitutionName.setResult(new Result<>(educationalRegionDto));
		return getEducationalInstitutionName;

	}

	@Override
	public Response<PaginationResponse<List<EducationRegionGetDto>>> getAllEducationalInstitutions(Integer pageNumber,Integer pageSize) {

		Result<PaginationResponse<List<EducationRegionGetDto>>> allEducationalResult = new Result<>();



		Pageable paging = PageRequest.of(pageNumber, pageSize);

		Response<PaginationResponse<List<EducationRegionGetDto>>> getListofEducationalInstitution = new Response<>();



		Page<EducationalInstitution> list = this.educationalInstitutionRepository.findAll(paging);

		List<EducationRegionGetDto> EducationalRegionDtoList = new ArrayList<>();
		for (EducationalInstitution eduInsti : list) {
			EducationRegionGetDto educationalRegionDto = new EducationRegionGetDto();

			educationalRegionDto.setEducationalInstituteDto(educationalInstitutionMapper.entityToDtos(eduInsti));
			Set<RegionGet> regionDtos = eduInsti.getRegion().stream().map(region -> regionMapper.toDtos(region))
					.collect(Collectors.toSet());
			educationalRegionDto.setRegionDto(regionDtos);
			EducationalRegionDtoList.add(educationalRegionDto);

		}

		if (list.isEmpty()) {
			throw new CustomException(HttpStatusCode.NO_EDUCATIONAL_INSTITUTION_FOUND.getCode(),
					HttpStatusCode.NO_EDUCATIONAL_INSTITUTION_FOUND,
					HttpStatusCode.NO_EDUCATIONAL_INSTITUTION_FOUND.getMessage(), allEducationalResult);
		}

		PaginationResponse paginationResponse=new PaginationResponse<List<EducationRegionGetDto>>(EducationalRegionDtoList,list.getTotalPages(),list.getTotalElements());

		allEducationalResult.setData(paginationResponse);
		getListofEducationalInstitution
				.setStatusCode(HttpStatusCode.EDUCATIONAL_INSTITUTION_RETRIVED_SUCCESSFULLY.getCode());
		getListofEducationalInstitution
				.setMessage(HttpStatusCode.EDUCATIONAL_INSTITUTION_RETRIVED_SUCCESSFULLY.getMessage());
		getListofEducationalInstitution.setResult(allEducationalResult);
		return getListofEducationalInstitution;
	}

	@Override
	public Response<EducationalInstitutionDto> deleteEducationalInstitution(int id) {

		Result<EducationalInstitutionDto> res = new Result<>();
		res.setData(null);
		Optional<EducationalInstitution> educationalInst = educationalInstitutionRepository.findById(id);
		if (!educationalInst.isPresent()) {
			throw new CustomException(HttpStatusCode.RESOURCE_NOT_FOUND.getCode(), HttpStatusCode.RESOURCE_NOT_FOUND,
					HttpStatusCode.RESOURCE_NOT_FOUND.getMessage(), res);
		}

		for (Region region : educationalInst.get().getRegion()) {
			region.getEducationalInstitiute().remove(educationalInst.get());
			regionRepository.save(region);
		}

		educationalInst.get().setRegion(new HashSet<>());
		educationalInstitutionRepository.save(educationalInst.get());

		educationalInstitutionRepository.deleteById(id);
		Response<EducationalInstitutionDto> response = new Response<>();
		response.setMessage(HttpStatusCode.EDUCATIONAL_INSTITUTION_DELETED.getMessage());
		response.setStatusCode(HttpStatusCode.EDUCATIONAL_INSTITUTION_DELETED.getCode());
		response.setResult(
				new Result<EducationalInstitutionDto>(educationalInstitutionMapper.entityToDto(educationalInst.get())));
		return response;
	}

	@Override
	public Response<EducationalRegionDto> updateEducationalInstitution(
			EducationalInstitutionDto educationalInstitutionDto) {

		Result<EducationalRegionDto> res = new Result<>();

		res.setData(null);
		Optional<EducationalInstitution> existingEducationalContainer = educationalInstitutionRepository
				.findById(educationalInstitutionDto.getId());
		if (!existingEducationalContainer.isPresent()) {
			throw new CustomException(HttpStatusCode.NO_EDUCATIONAL_INSTITUTION_FOUND.getCode(),
					HttpStatusCode.NO_EDUCATIONAL_INSTITUTION_FOUND,
					HttpStatusCode.NO_EDUCATIONAL_INSTITUTION_FOUND.getMessage(), res);
		}
		EducationalInstitutionDto existingEducationalInstitution = educationalInstitutionMapper
				.entityToDto(existingEducationalContainer.get());
		existingEducationalInstitution
				.setEducationalInstitutionCode(educationalInstitutionDto.getEducationalInstitutionCode());
		existingEducationalInstitution
				.setEducationalInstitutionName(educationalInstitutionDto.getEducationalInstitutionName());
		existingEducationalInstitution
				.setEducationalInstitutionType(educationalInstitutionDto.getEducationalInstitutionType());
		existingEducationalInstitution.setExemptionFlag(educationalInstitutionDto.getExemptionFlag());
		existingEducationalInstitution.setState(educationalInstitutionDto.getState());
		existingEducationalInstitution.setStrength(educationalInstitutionDto.getStrength());
		existingEducationalInstitution.setVvnAccount(educationalInstitutionDto.getVvnAccount());
		existingEducationalInstitution.setRegionId(new HashSet<>());

		for (Integer regionId : educationalInstitutionDto.getRegionId()) {
			Region region = regionRepository.getReferenceById(regionId);
			System.out.println("region --- " + region.toString());
			if(region != null) existingEducationalInstitution.getRegionId().add(regionId);
		}

		if (educationalInstitutionDto.getRegionId().isEmpty()) {
			throw new CustomException(HttpStatusCode.NO_REGION_ADDED.getCode(), HttpStatusCode.NO_REGION_ADDED,
					HttpStatusCode.NO_REGION_ADDED.getMessage(), res);
		}

		EducationalInstitution ei1 = educationalInstitutionMapper.dtoToEntity(existingEducationalInstitution);
		EducationalInstitution updateEducationalInst = educationalInstitutionRepository.save(ei1);
		EducationalRegionDto educationalRegionDto = educationalInstitutionMapper
				.toEducationalRegionDto(updateEducationalInst);
		Response<EducationalRegionDto> response = new Response<>();
		response.setMessage(HttpStatusCode.EDUCATIONAL_INSTITUTION_UPDATED.getMessage());
		response.setStatusCode(HttpStatusCode.EDUCATIONAL_INSTITUTION_UPDATED.getCode());
		response.setResult(new Result<EducationalRegionDto>(educationalRegionDto));
		return response;
	}

	@Override
	public Response<EducationRegionGetDto> getEduInstwithRegion(int id) {

		Response<EducationRegionGetDto> response = new Response<>();
		Result<EducationRegionGetDto> res = new Result<>();

		Optional<EducationalInstitution> educationalInst = this.educationalInstitutionRepository.findById(id);

		if (!educationalInst.isPresent()) {
			throw new CustomException(HttpStatusCode.NO_EDUCATIONAL_INSTITUTION_MATCH_WITH_ID.getCode(),
					HttpStatusCode.NO_EDUCATIONAL_INSTITUTION_MATCH_WITH_ID,
					HttpStatusCode.NO_EDUCATIONAL_INSTITUTION_MATCH_WITH_ID.getMessage(), res);
		}
		EducationRegionGetDto educationalRegionDto = new EducationRegionGetDto();
		educationalRegionDto
				.setEducationalInstituteDto(educationalInstitutionMapper.entityToDtos(educationalInst.get()));
		educationalRegionDto.setRegionDto(regionMapper.entitiesToDtos(educationalInst.get().getRegion()));

		res.setData(educationalRegionDto);

		response.setStatusCode(HttpStatusCode.EDUCATIONAL_INSTITUTION_RETRIVED_SUCCESSFULLY.getCode());
		response.setMessage(HttpStatusCode.EDUCATIONAL_INSTITUTION_RETRIVED_SUCCESSFULLY.getMessage());
		response.setResult(new Result<>(educationalRegionDto));
		return response;

	}

//	@Override
//	public ByteArrayInputStream load() {
//		List<EducationalInstitution> eduInst = educationalInstitutionRepository.findAll();
//		ByteArrayInputStream out = EducationalInstitutionCsvHelper.educationCSV(eduInst);
//		return out;
//	}

	@Override
	public Response<List<EducationalInstitutionDto>> getEduInstwithSort(String field) {

		Result<List<EducationalInstitutionDto>> allEducationalResult = new Result<>();

		Response<List<EducationalInstitutionDto>> getListofEducationalInstitution = new Response<>();

		List<EducationalInstitution> list = this.educationalInstitutionRepository
				.findAll(Sort.by(Sort.Direction.ASC, field));
		List<EducationalInstitutionDto> educationalInstitutionDtos = educationalInstitutionMapper.entitiesToDtos(list);

		if (list.size() == 0) {
			throw new CustomException(HttpStatusCode.NO_EDUCATIONAL_INSTITUTION_FOUND.getCode(),
					HttpStatusCode.NO_EDUCATIONAL_INSTITUTION_FOUND,
					HttpStatusCode.NO_EDUCATIONAL_INSTITUTION_FOUND.getMessage(), allEducationalResult);
		}
		allEducationalResult.setData(educationalInstitutionDtos);
		getListofEducationalInstitution
				.setStatusCode(HttpStatusCode.EDUCATIONAL_INSTITUTION_RETRIVED_SUCCESSFULLY.getCode());
		getListofEducationalInstitution
				.setMessage(HttpStatusCode.EDUCATIONAL_INSTITUTION_RETRIVED_SUCCESSFULLY.getMessage());
		getListofEducationalInstitution.setResult(allEducationalResult);
		return getListofEducationalInstitution;
	}

}

package com.ubi.MasterService.service;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.ubi.MasterService.util.PermissionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.ubi.MasterService.dto.educationalInstitutiondto.EducationalInstitutionDto;
import com.ubi.MasterService.dto.regionDto.EIRegionMappingDto;
import com.ubi.MasterService.dto.regionDto.EducationalRegionDto;
import com.ubi.MasterService.dto.response.Response;
import com.ubi.MasterService.entity.EducationalInstitution;
import com.ubi.MasterService.entity.Region;
import com.ubi.MasterService.error.CustomException;
import com.ubi.MasterService.error.HttpStatusCode;
import com.ubi.MasterService.error.Result;
import com.ubi.MasterService.mapper.EducationalInstitutionMapper;
import com.ubi.MasterService.mapper.RegionMapper;
import com.ubi.MasterService.repository.EducationalInstitutionRepository;
import com.ubi.MasterService.repository.RegionRepository;


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

	@Autowired
	PermissionUtil permissionUtil;

	Logger logger = LoggerFactory.getLogger(EducationalInstitutionServiceImpl.class);

	@Override
	public Response<EducationalInstitutionDto> addEducationalInstitution(
			EducationalInstitutionDto educationalInstitutionDto) {

		Result<EducationalInstitutionDto> res = new Result<>();

		Response<EducationalInstitutionDto> response = new Response<>();
		Optional<EducationalInstitution> tempeducationalInstitution = educationalInstitutionRepository
				.findById(educationalInstitutionDto.getId());
		
        EducationalInstitution educationalInstitutionName=educationalInstitutionRepository.getEducationalInstitutionByeducationalInstitutionName(educationalInstitutionDto.getEducationalInstitutionName());
		
		EducationalInstitution educationalInstitutionCode=educationalInstitutionRepository.getEducationalInstitutionByeducationalInstitutionCode(educationalInstitutionDto.getEducationalInstitutionCode());
		

		if (tempeducationalInstitution.isPresent()) {
			throw new CustomException(HttpStatusCode.NO_EDUCATIONAL_INSTITUTION_FOUND.getCode(),
					HttpStatusCode.NO_EDUCATIONAL_INSTITUTION_FOUND,
					HttpStatusCode.NO_EDUCATIONAL_INSTITUTION_FOUND.getMessage(), res);
		}
		
		if(educationalInstitutionName !=null)
		{
			throw new CustomException(HttpStatusCode.EDUCATIONAL_INSTITUTION_NAME_ALREADY_EXISTS.getCode(),
					HttpStatusCode.EDUCATIONAL_INSTITUTION_NAME_ALREADY_EXISTS,
					HttpStatusCode.EDUCATIONAL_INSTITUTION_NAME_ALREADY_EXISTS.getMessage(), res);
		}
		
		if(educationalInstitutionCode!=null)
		{
			throw new CustomException(HttpStatusCode.EDUCATIONAL_INSTITUTION_CODE_ALREADY_EXISTS.getCode(),
					HttpStatusCode.EDUCATIONAL_INSTITUTION_CODE_ALREADY_EXISTS,
					HttpStatusCode.EDUCATIONAL_INSTITUTION_CODE_ALREADY_EXISTS.getMessage(), res);
		}
		
		EducationalInstitution saveEducationalInstitution = educationalInstitutionRepository
				.save(educationalInstitutionMapper.dtoToEntity(educationalInstitutionDto));
		response.setStatusCode(HttpStatusCode.RESOURCE_CREATED_SUCCESSFULLY.getCode());
		response.setMessage(HttpStatusCode.RESOURCE_CREATED_SUCCESSFULLY.getMessage());
		response.setResult(new Result<EducationalInstitutionDto>(
				educationalInstitutionMapper.entityToDto(saveEducationalInstitution)));
		return response;

	}

	@Override
	public Response<EducationalInstitutionDto> getSingleEducationalInstitution(int id) {

		Response<EducationalInstitutionDto> getEducationalInstitution = new Response<>();
		Optional<EducationalInstitution> educationalInst =this.educationalInstitutionRepository.findById(id);
		Result<EducationalInstitutionDto> educationalResult = new Result<>();
		if (!educationalInst.isPresent()) {
			throw new CustomException(HttpStatusCode.NO_EDUCATIONAL_INSTITUTION_MATCH_WITH_ID.getCode(),
					HttpStatusCode.NO_EDUCATIONAL_INSTITUTION_MATCH_WITH_ID,
					HttpStatusCode.NO_EDUCATIONAL_INSTITUTION_MATCH_WITH_ID.getMessage(), educationalResult);
		}
		educationalResult.setData(educationalInstitutionMapper.entityToDto(educationalInst.get()));
		getEducationalInstitution.setStatusCode(HttpStatusCode.EDUCATIONAL_INSTITUTION_RETRIVED_SUCCESSFULLY.getCode());
		getEducationalInstitution.setMessage(HttpStatusCode.EDUCATIONAL_INSTITUTION_RETRIVED_SUCCESSFULLY.getMessage());
		getEducationalInstitution.setResult(educationalResult);
		return getEducationalInstitution;
	}

	@Override
	public Response<EducationalInstitutionDto> getEducationalInstituteByName(String educationalInstitutionName) {

		Result<EducationalInstitutionDto> res = new Result<>();
		res.setData(null);
		Response<EducationalInstitutionDto> getEducationalInstitutionName = new Response<>();
		Optional<EducationalInstitution> educationalInst =this.educationalInstitutionRepository
				.findByeducationalInstitutionName(educationalInstitutionName);
		Result<EducationalInstitutionDto> educationalInstitutionResult = new Result<>();
		if (!educationalInst.isPresent()) {
			throw new CustomException(HttpStatusCode.NO_EDUCATIONAL_INSTITUTION_NAME_FOUND.getCode(),
					HttpStatusCode.NO_EDUCATIONAL_INSTITUTION_NAME_FOUND,
					HttpStatusCode.NO_EDUCATIONAL_INSTITUTION_NAME_FOUND.getMessage(), res);
		}
		educationalInstitutionResult.setData(educationalInstitutionMapper.entityToDto(educationalInst.get()));
		getEducationalInstitutionName
				.setStatusCode(HttpStatusCode.EDUCATIONAL_INSTITUTION_RETRIVED_SUCCESSFULLY.getCode());
		getEducationalInstitutionName
				.setMessage(HttpStatusCode.EDUCATIONAL_INSTITUTION_RETRIVED_SUCCESSFULLY.getMessage());
		getEducationalInstitutionName.setResult(educationalInstitutionResult);
		return getEducationalInstitutionName;
	}

	@Override
	public Response<List<EducationalInstitutionDto>> getAllEducationalInstitutions(Integer pageNumber,
			Integer pageSize) {

		Result<List<EducationalInstitutionDto>> allEducationalResult = new Result<>();
		Pageable paging = PageRequest.of(pageNumber, pageSize);
		Response<List<EducationalInstitutionDto>> getListofEducationalInstitution = new Response<>();

		Page<EducationalInstitution> list = this.educationalInstitutionRepository.findAll(paging);
		List<EducationalInstitutionDto> educationalInstitutionDtos = educationalInstitutionMapper
				.entitiesToDtos(list.toList());

		if (list.getSize() == 0) {
			throw new CustomException(HttpStatusCode.NO_EDUCATIONAL_INSTITUTION_FOUND.getCode(), HttpStatusCode.NO_EDUCATIONAL_INSTITUTION_FOUND,
					HttpStatusCode.NO_EDUCATIONAL_INSTITUTION_FOUND.getMessage(), allEducationalResult);
		}
		allEducationalResult.setData(educationalInstitutionDtos);
		getListofEducationalInstitution.setStatusCode(HttpStatusCode.EDUCATIONAL_INSTITUTION_RETRIVED_SUCCESSFULLY.getCode());
		getListofEducationalInstitution.setMessage(HttpStatusCode.EDUCATIONAL_INSTITUTION_RETRIVED_SUCCESSFULLY.getMessage());
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
		educationalInstitutionRepository.deleteById(id);
		Response<EducationalInstitutionDto> response = new Response<>();
		response.setMessage(HttpStatusCode.EDUCATIONAL_INSTITUTION_DELETED.getMessage());
		response.setStatusCode(HttpStatusCode.EDUCATIONAL_INSTITUTION_DELETED.getCode());
		response.setResult(
				new Result<EducationalInstitutionDto>(educationalInstitutionMapper.entityToDto(educationalInst.get())));
		return response;
	}

	@Override
	public Response<EducationalInstitutionDto> updateEducationalInstitution(
			EducationalInstitutionDto educationalInstitutionDto) {

		Result<EducationalInstitutionDto> res = new Result<>();

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

		EducationalInstitution updateEducationalInst = educationalInstitutionRepository
				.save(educationalInstitutionMapper.dtoToEntity(existingEducationalInstitution));
		Response<EducationalInstitutionDto> response = new Response<>();
		response.setMessage(HttpStatusCode.EDUCATIONAL_INSTITUTION_UPDATED.getMessage());
		response.setStatusCode(HttpStatusCode.EDUCATIONAL_INSTITUTION_UPDATED.getCode());
		response.setResult(new Result<>(educationalInstitutionMapper.entityToDto(updateEducationalInst)));
		return response;
	}

	@Override
	public Response<EducationalRegionDto> addRegion(EIRegionMappingDto eIRegionMappingDto) {
		int eduId = eIRegionMappingDto.getEducationalInstitutionId();
		int regionId = eIRegionMappingDto.getRegionid();
		Response<EducationalRegionDto> response = new Response<>();
		Result<EducationalRegionDto> res = new Result<>();
		EducationalInstitution eduInstitute = educationalInstitutionRepository.getReferenceById(eduId);
		Region region = regionRepository.getReferenceById(regionId);
		Set<Region> setOfRegion = eduInstitute.getRegion();
		for (Region currRegion : setOfRegion) {
			if (currRegion.getId() == region.getId()) {
				throw new CustomException(HttpStatusCode.MAPPING_ALREADY_EXIST.getCode(),
						HttpStatusCode.MAPPING_ALREADY_EXIST, HttpStatusCode.MAPPING_ALREADY_EXIST.getMessage(), res);
			}
		}
		eduInstitute.getRegion().add(region);
		region.getEducationalInstitiute().add(eduInstitute);
		//region.getEducationalInstitiute().add(eduInstitute);
		//region.getEducationalInstitiute().add(eduInstitute);
		regionRepository.save(region);
		educationalInstitutionRepository.save(eduInstitute);
		EducationalRegionDto educationalRegionDto = educationalInstitutionMapper.toEducationalRegionDto(eduInstitute);
		response.setStatusCode(HttpStatusCode.SUCCESSFUL.getCode());
		response.setMessage(HttpStatusCode.SUCCESSFUL.getMessage());
		response.setResult(new Result<>(educationalRegionDto));
		return response;
	}



	@Override
	public Response<EducationalRegionDto> getEduInstwithRegion(int id) {

		Response<EducationalRegionDto> response = new Response<>();
		Result<EducationalRegionDto> res = new Result<>();

		Optional<EducationalInstitution> educationalInst = this.educationalInstitutionRepository.findById(id);

		if (!educationalInst.isPresent()) {
			throw new CustomException(HttpStatusCode.NO_EDUCATIONAL_INSTITUTION_MATCH_WITH_ID.getCode(),
					HttpStatusCode.NO_EDUCATIONAL_INSTITUTION_MATCH_WITH_ID,
					HttpStatusCode.NO_EDUCATIONAL_INSTITUTION_MATCH_WITH_ID.getMessage(), res);
		}
		EducationalRegionDto educationalRegionDto = new EducationalRegionDto();
		educationalRegionDto.setEducationalInstituteDto(educationalInstitutionMapper.entityToDto(educationalInst.get()));
		educationalRegionDto.setRegionDto(regionMapper.entitiesToDto(educationalInst.get().getRegion()));

		res.setData(educationalRegionDto);

		response.setStatusCode(HttpStatusCode.EDUCATIONAL_INSTITUTION_RETRIVED_SUCCESSFULLY.getCode());
		response.setMessage(HttpStatusCode.EDUCATIONAL_INSTITUTION_RETRIVED_SUCCESSFULLY.getMessage());
		response.setResult(new Result<>(educationalRegionDto));
		return response;

	}
	
	
//	@Override
//	public ByteArrayInputStream load() {
//		List<EducationalInstitution> eduInst=educationalInstitutionRepository.findAll();
//        ByteArrayInputStream out = EducationalInstitutionCsvHelper.educationCSV(eduInst);
//	    return out;
//	  }

	@Override
	public Response<List<EducationalInstitutionDto>> getEduInstwithSort(String field) {
	
		Result<List<EducationalInstitutionDto>> allEducationalResult = new Result<>();
	//	Pageable paging = PageRequest.of(pageNumber, pageSize);
		Response<List<EducationalInstitutionDto>> getListofEducationalInstitution = new Response<>();

		List<EducationalInstitution> list = this.educationalInstitutionRepository.findAll(Sort.by(Sort.Direction.ASC,field));
		List<EducationalInstitutionDto> educationalInstitutionDtos = educationalInstitutionMapper
				.entitiesToDtos(list);

		if (list.size() == 0) {
			throw new CustomException(HttpStatusCode.NO_EDUCATIONAL_INSTITUTION_FOUND.getCode(), HttpStatusCode.NO_EDUCATIONAL_INSTITUTION_FOUND,
					HttpStatusCode.NO_EDUCATIONAL_INSTITUTION_FOUND.getMessage(), allEducationalResult);
		}
		allEducationalResult.setData(educationalInstitutionDtos);
		getListofEducationalInstitution.setStatusCode(HttpStatusCode.EDUCATIONAL_INSTITUTION_RETRIVED_SUCCESSFULLY.getCode());
		getListofEducationalInstitution.setMessage(HttpStatusCode.EDUCATIONAL_INSTITUTION_RETRIVED_SUCCESSFULLY.getMessage());
		getListofEducationalInstitution.setResult(allEducationalResult);
		return getListofEducationalInstitution;
	}
	

}

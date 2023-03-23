package com.ubi.masterservice.error;

public enum HttpStatusCode {

	NO_ENTRY_FOUND(101,"Resource Not Found"),

	NO_STUDENT_NAME_FOUND(202,"No student name found"),

	NO_CLASSID_FOUND(206,"please enter class Id"),

	NO_SCHOOL_NAME_FOUND(202, "No School Name Found"),

	NO_STUDENT_FOUND(108,"No Student Found"),

	NO_REGION_FOUND(108,"No Region Found"),

	ENTER_PROPER_AADHAAR_NO(109,"Enter 12 digit aadhar no"),
	
	ENTER_PROPER_MOBILE_NO(109,"Enter 10 digit mobile no"),
	
	ENTER_PROPER_EMAIL_ID(109,"Enter proper email id"),

	NO_REGION_ADDED(108,"Add a region in Educational Institution"),

	NO_SCHOOL_ADDED(108,"Add a School in Class"),

	NO_EDUCATIONAL_INSTITUTE_ADDED(108,"Invalid institute is being sent to map with region"),

	NO_CONTENT(204,"Content Not Found"),


	CLASS_RETRIVED_SUCCESSFULLY(200,"class Retrived"),

	NO_STUDENT_MATCH_WITH_ID(109,"No such student found with such id"),

	NO_CLASS_MATCH_WITH_ID(109,"No such class found with such id"),

	NO_EDUCATIONAL_INSTITUTION_FOUND(108,"No Educational Institution Found"),

	NO_EDUCATIONAL_INSTITUTION_MATCH_WITH_ID(109,"No Educational Institution found with given Id "),

	NO_EDUCATIONAL_INSTITUTION_NAME_FOUND(202,"No Educational Institution Name Found"),

	EDUCATIONAL_INSTITUTION_RETRIVED_SUCCESSFULLY(200,"Educational Institution Retrived"),

	EDUCATIONAL_INSTITUTION_NAME_ALREADY_EXISTS(110, "Educational Institution Name Already exists"),

	EDUCATIONAL_INSTITUTION_CODE_ALREADY_EXISTS(110, "Educational Institution Code Already exists"),

	REGION_CODE_DUPLICATE(110,"region with given code already exist"),

	REGION_NAME_DUPLICATE(110,"region with given name already exist"),

	NO_SCHOOL_MATCH_WITH_ID(109, "No School Found with Given ID"),

	NO_SCHOOL_FOUND(108, "No School Found"),
	

	NO_CLASS_FOUND(108, "No Class Found"),

	NO_SCHOOL_MATCH_WITH_NAME(110, "No School Found With Given NAME"),

	SCHOOL_NAME_ALREADY_EXISTS(110, "School Name Already Exist"),

	SCHOOL_CODE_ALREADY_EXISTS(110, "School Code Already Exist"),

	RESOURCE_NOT_FOUND(404, "Does not exist"),

	RESOURCE_ALREADY_DELETED(410, "Does not exist"),
	RESOURCE_ALREADY_EXISTS(409, "Already exists"),

	BAD_REQUEST_EXCEPTION(400, "Bad Request Occuured"),


	UNAUTHORIZED_EXCEPTION(401, "Unauthorized To Perform Request"),

	USER_DEACTIVATED(401, "User is deactivated"),
	TOKEN_FORMAT_INVALID(401, "Token Format Invalid"),
	PERMISSION_DENIED(401, "Permission To Perform This Action Denied"),


	SUCCESSFUL(200, "Request Successfull"),


	STUDENT_DELETED(200, "Student Deleted Successfully"),
	STUDENT_PROMOTED_SUCCESSFULLY(200, "Student promoted Successfully"),
	STUDENT_DEMOTED_SUCCESSFULLY(200, "Student Demoted Successfully"),


	STUDENT_UPDATED(200, "Student Updated Successfully"),

	//CLASS_DELETED(200, "class deleted successfully"),

	CLASS_UPDATED(200, "Class updated successfully"),

	PAYMENT_UPDATED(200, "Payment updated successfully"),
	PAYMENT_DELETED(200,"Payment deleted successfully"),

	EDUCATIONAL_INSTITUTION_DELETED(200,"Educational Institution deleted successfully"),
	EDUCATIONAL_INSTITUTION_UPDATED(200,"Educational Institution updated successfully"),


	SCHOOL_DELETED_SUCCESSFULLY(200,"School Deleted Successfully"),
	SCHOOL_UPDATED(200,"School Updated Successfully"),

	NO_CLASS_ADDED(200, "No Class Added"),


	RESOURCE_CREATED_SUCCESSFULLY(201, "Resource Created Successfully"),


	TRANSFER_CERTIFICATE_UPDATED(200, "Transfer Certificate Updated Successfully"),




	REGION_RETRIEVED_SUCCESSFULLY(200,"Region Retrieved Succesfully"),

	CLASS_RETREIVED_SUCCESSFULLY(200, "Class Retrieved Successfully"),


	SCHOOL_RETRIVED_SUCCESSFULLY(200,"School Retrieved Successfully"),

	REGION_RETREIVED_SUCCESSFULLY(200,"Region Retrieved Succesfully"),

	REGION_DELETED_SUCCESSFULLY(200,"Region Deleted SuccessFully"),

	CLASS_DELETED_SUCCESSFULLY(200, "Class Deleted Successfully"),

	TEACHER_NOT_VERIFIED(200, "Not verified by Teacher"),

	REGION_NOT_FOUND(108,"No such region found"),

	CLASS_NOT_FOUND(108, "No Class Found"),
	
	NO_COLLEGES_FOUND(108, "No College Found"),

	REGION_UPDATED(200,"Region Updated Successfully"),

	STUDENT_VERIFIED_SUCCESSFULLY(200,"Student verified successfully"),

	MAPPING_ALREADY_EXIST(108,"Mapping Already Exist");



	private int code;
	private String message;

	HttpStatusCode(int code, String message) {
		this.code = code;
		this.message = message;
	}

	public int getCode() {
		return code;
	}

	public String getMessage() {
		return message;
	}

}
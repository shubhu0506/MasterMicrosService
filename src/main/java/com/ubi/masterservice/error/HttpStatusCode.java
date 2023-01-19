package com.ubi.masterservice.error;

public enum HttpStatusCode {

	NO_ENTRY_FOUND(101,"Resource Not Found"),

	NO_STUDENT_NAME_FOUND(202,"No student name found"),

	NO_CLASSID_FOUND(206,"please enter class Id"),

	NO_SCHOOL_NAME_FOUND(202, "No School Name Found"),

	NO_STUDENT_FOUND(108,"No Student Found"),

	NO_REGION_FOUND(108,"No Region Found"),

	NO_REGION_ADDED(108,"Add a region in Educational Institution"),

	NO_REGION_ID_FOUND(206,"please enter Region Id"),

	USER_RETRIVED_SUCCESSFULLY(200,"User Retrived"),

	CLASS_RETRIVED_SUCCESSFULLY(200,"class Retrived"),

	NO_USER_MATCH_WITH_ID(109,"No User found with given Id "),
	
    USER_NOT_EXISTS(110, "User with given roleType not exist"),
	
	USER_WITH_ROLE_PRINCIPAL_NOT_EXISTS(110, "User with Principal roleType does not exist"),

	NO_STUDENT_MATCH_WITH_ID(109,"No such student found with such id"),

	NO_CLASS_MATCH_WITH_ID(109,"No such class found with such id"),

	NO_PAYMENT_FOUND(108,"No Payment Found"),

	NO_PAYMENT_MATCH_WITH_ID(109,"No Payment found with given Id "),

	PAYMENT_RETRIVED_SUCCESSFULLY(200,"Payment Retrived"),

	STUDENT_RETRIVED_SUCCESSFULLY(200,"Student retrived"),
	
	COLLEGE_RETRIVED_SUCCESSFULLY(200, "Colleges Retrived"),

	CONTACTINFO_RETRIVED_SUCCESSFULLY(200,"Contact Info retrived successfully"),

	NO_CLASSCODE_FOUND(202,"No class code found"),

	//NO_CLASS_FOUND(108,"No class found"),

	NO_CONTACTINFO_FOUND(108,"No contact info found"),

	NO_EDUCATIONAL_INSTITUTION_FOUND(108,"No Educational Institution Found"),

	NO_EDUCATIONAL_INSTITUTION_MATCH_WITH_ID(109,"No Educational Institution found with given Id "),

	NO_EDUCATIONAL_INSTITUTION_NAME_FOUND(202,"No Educational Institution Name Found"),

	EDUCATIONAL_INSTITUTION_RETRIVED_SUCCESSFULLY(200,"Educational Institution Retrived"),

	EDUCATIONAL_INSTITUTION_NAME_ALREADY_EXISTS(110, "Educational Institution Name Already exists"),

	EDUCATIONAL_INSTITUTION_CODE_ALREADY_EXISTS(110, "Educational Institution Code Already exists"),

	REGION_CODE_DUPLICATE(110,"region with given code already exist"),

	REGION_NAME_DUPLICATE(110,"region with given name already exist"),
	NO_TRANSFERCERTIFICATE_FOUND(108, "No Transfer Certificate Found"),

	NO_CONTACTINFO_MATCH_WITH_ID(109,"No such contact info found with such id"),

	NO_REGION_MATCH_WITH_ID(109, "No Region Match with ID"),

	NO_TRANSFER_CERTIFICATE_MATCH_WITH_ID(109,"No such Transfer Certificate found with such id"),

	NO_SCHOOL_MATCH_WITH_ID(109, "No School Found with Given ID"),

	NO_SCHOOL_FOUND(108, "No School Found"),
	
	NO_COLLEGE_FOUND(108, "No School Found"),

	NO_CLASS_FOUND(108, "No Class Found"),

	NO_SCHOOL_MATCH_WITH_NAME(110, "No School Found With Given NAME"),

	SCHOOL_NAME_ALREADY_EXISTS(110, "School Name Already Exist"),

	SCHOOL_CODE_ALREADY_EXISTS(110, "School Code Already Exist"),

	RESOURCE_NOT_FOUND(108, "Does not exist"),

	RESOURCE_ALREADY_EXISTS(110, "Already exists"),
	ROLE_NOT_EXISTS(110, "Given Role Type Not Exist"),
	USERNAME_NOT_AVAILAIBLE(110, "Username Not Availaible"),
	ROLETYPE_NOT_AVAILAIBLE(110, "Role Type Not Availaible"),
	INVALID_COLUMN_NAME(111, "Invalid column name provided"),

	WRONG_DATA_TYPE(112, "Wrong datatype selected for non multivalued field"),

	IO_EXCEPTION(113, "I/O exception occurred"),

	JSON_PARSE_EXCEPTION(114, "JSON parse error occurred"),

	INVALID_FIELD_VALUE(116, "Value for field : {} is not expected as : {}"),

	CONNECTION_REFUSED(120, "Connection is refused from the server"),

	INVALID_CREDENTIALS(121, "Invalid credentials provided"),

	BAD_REQUEST_EXCEPTION(400, "Bad Request Occuured"),

	NULL_POINTER_EXCEPTION(500, "Received Null response"),

	SERVER_UNAVAILABLE(503, "Unable to Connect To the Server"),

	OPERATION_NOT_ALLOWED(405, "Operation is Not Allowed"),

	UNAUTHORIZED_EXCEPTION(401, "Unauthorized To Perform Request"),
	USER_DEACTIVATED(401, "User is deactivated"),
	TOKEN_FORMAT_INVALID(401, "Token Format Invalid"),
	PERMISSION_DENIED(401, "Permission To Perform This Action Denied"),
	FORBIDDEN_EXCEPTION(403, "Forbidden access attempted"),

	INTERNAL_SERVER_ERROR(500, "Internal Server Error Occured"),

	PROCESSING_NOT_COMPLETED(202, "Request cannot be Processed"),

	NOT_ACCEPTABLE_ERROR(406, "Request Not accpetable"),

	SUCCESSFUL(200, "Request Successfull"),

	CONTACTINFO_DELETED(200,"contact info deleted successfully"),

	TRANSFER_CERTIFICATE_DELETED(200, "Transfer Certificate Deleted Successfully"),

	RETREIVED_SUCCESSFULLY(200, "Data Retrieved Successfully"),

	STUDENT_DELETED(200, "Student Deleted Successfully"),
	STUDENT_PROMOTED_SUCCESSFULLY(200, "Student promoted Successfully"),
	STUDENT_DEMOTED_SUCCESSFULLY(200, "Student Demoted Successfully"),
	CONTACTINFO_UPDATED(200,"Contact info updated successfully"),


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
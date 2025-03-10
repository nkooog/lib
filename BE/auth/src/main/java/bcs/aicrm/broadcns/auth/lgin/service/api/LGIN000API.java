package bcs.aicrm.broadcns.auth.lgin.service.api;

import bcs.aicrm.broadcns.auth.lgin.model.DTO.LGIN000DTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "LGIN000Controller", description = "로그인 관련 API")
public interface LGIN000API {

	@Parameters({
			@Parameter(name = "tenantId", description = "테넌트 ID", example = "DMO"),
			@Parameter(name = "usrId", description = "사용자 ID", example = "000000"),
			@Parameter(name = "scrtNo", description = "비밀번호")
	})
	@Operation(summary = "crm 로그인", description = "ncrm 로그인 관련 인증 및 토큰 발급")
	@ApiResponses(value ={
			@ApiResponse(responseCode = "201", description = "로그인 성공 및 토큰 발급", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
			@ApiResponse(responseCode = "400 ", description = "입력정보 불일치", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
			@ApiResponse(responseCode = "403 ", description = "토큰 만료", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
			@ApiResponse(responseCode = "404 ", description = "사용자 정보 없음", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
	})
	ResponseEntity login(@RequestBody @Valid LGIN000DTO lgin000DTO, Errors errors) throws Exception;


}

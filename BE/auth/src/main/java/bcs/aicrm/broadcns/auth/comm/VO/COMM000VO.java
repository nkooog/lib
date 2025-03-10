package bcs.aicrm.broadcns.auth.comm.VO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@SuperBuilder
public class COMM000VO {

	@NotNull(message = "tenantId is required")
	@Schema(hidden = true)
	private String tenantId;
	private String dmnCd;
	private String tenantStCd;
	private String tenantStRsnCd;
	private String fmnm;
	private String fmnmEng;
	private String reprNm;
	private String reprNmEng;
	private String svcTypCd;
	private String spTypCd;
	private String usrAcCnt;
	private String emlSndGrpsAddr;
	private String mlingCd;
	private String orgLvlCd;
	private String svcContDd;
	private String svcBltnDd;
	private String svcExpryDd;
	private String svcTrmnDd;
	private String regDtm;
	private String regrId;
	private String regrOrgCd;
	private String lstCorcDtm;
	private String lstCorprId;
	private String lstCorprOrgCd;
	private String abolDtm;
	private String abolmnId;
	private String abolmnOrgCd;
}

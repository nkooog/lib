package bcs.aicrm.broadcns.comm.sysm.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {

	@PostMapping("/test")
	public ResponseEntity test() throws Exception {
		return ResponseEntity.ok().body("comm service test");
	}

}

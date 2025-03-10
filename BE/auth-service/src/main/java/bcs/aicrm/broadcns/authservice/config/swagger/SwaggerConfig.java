package bcs.aicrm.broadcns.authservice.config.swagger;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

	private final String TITLE = "JWT";

	@Bean
	public OpenAPI openAPI() {
		SecurityRequirement securityRequirement = new SecurityRequirement().addList(this.TITLE);
		Components components = new Components().addSecuritySchemes(this.TITLE, new SecurityScheme()
				.name(this.TITLE)
				.type(SecurityScheme.Type.HTTP)
				.scheme("Bearer")
				.bearerFormat(this.TITLE)
		);

		return new OpenAPI()
				.info(apiInfo())
				.addSecurityItem(securityRequirement)
				.components(components);
	}

	private Info apiInfo() {
		return new Info()
				.title("ncrm API Test")
				.description("ncrm BE API document")
				.version("2.0.0v");
	}

}

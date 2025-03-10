package bcs.aicrm.broadcns.auth.config;


import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.io.HttpClientConnectionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

@Configuration
public class BeanConfig {

	@Bean
	public RestTemplate restTemplate () {
		HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();

		HttpClient httpClient = HttpClientBuilder.create()
				.setConnectionManager(createHttpClientConnectionManager())
				.build();

		factory.setHttpClient(httpClient);
		RestTemplate restTemplate = new RestTemplate(factory);
		restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8));

		return restTemplate;
	}

	private HttpClientConnectionManager createHttpClientConnectionManager() {
		return PoolingHttpClientConnectionManagerBuilder.create()
				.setDefaultConnectionConfig(ConnectionConfig.custom()
						.setSocketTimeout(1000 * 30     , TimeUnit.MILLISECONDS)
						.setConnectTimeout(1000 * 30, TimeUnit.MILLISECONDS)
						.build())
				.build();
	}

}

package com.dataman.gitstats.configure;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;

import com.dataman.gitstats.properties.SwaggerProperties;

import io.swagger.annotations.ApiOperation;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.ResponseMessageBuilder;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ResponseMessage;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
/**
 * web api
 * @author 赵甜
 *
 */
@Configuration
@ConditionalOnProperty(prefix="swagger", name="enabled")
@EnableConfigurationProperties(SwaggerProperties.class)
@EnableSwagger2
public class SwaggerConfiguration {
	
	@Autowired
	private SwaggerProperties properties;
	
	private static List<ResponseMessage> responseMessageList = new ArrayList<ResponseMessage>();

	//状态提示
	static {
		responseMessageList.add(new ResponseMessageBuilder().code(500)
				.message("500 - Internal Server Error")
				.responseModel(new ModelRef("Error")).build());
		responseMessageList.add(new ResponseMessageBuilder().code(403)
				.message("403 - Forbidden").build());
	}

	@Bean
	@ConditionalOnMissingBean(Docket.class)
	public Docket api() {

		return new Docket(DocumentationType.SWAGGER_2)

		/**
		 * select() method returns an instance of ApiSelectorBuilder, which
		 * provides a way to control the endpoints exposed by Swagger
		 **/

		.select()

		/**
		 * Predicates for selection of RequestHandlers can be configured with
		 * the help of RequestHandlerSelectors and PathSelectors. Using any()
		 * for both will make documentation for your entire API available
		 * through Swagger.
		 **/

		.apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class)).paths(PathSelectors.any()).build()
		.pathMapping("/").apiInfo(metadata())
		/**
		 * Instructing Swagger not to use default response messages.
		 */
		.useDefaultResponseMessages(false)
		.globalResponseMessage(RequestMethod.GET, responseMessageList);
	}

	/*@Bean
	public UiConfiguration uiConfig() {
		return UiConfiguration.DEFAULT;
	}*/

	/**
	 * 源信息
	 * @return
	 */
	private ApiInfo metadata() {
		return new ApiInfoBuilder().title(properties.getTitle())
				.description(properties.getDescription()).version(properties.getVersion()).build();
	}
}

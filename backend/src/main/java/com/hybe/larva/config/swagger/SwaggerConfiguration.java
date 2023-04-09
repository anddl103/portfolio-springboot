package com.hybe.larva.config.swagger;

import com.hybe.larva.config.AppInfo;
import com.hybe.larva.consts.LarvaConst;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.*;
import springfox.documentation.schema.ScalarType;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
@Configuration
public class SwaggerConfiguration {

    private static final String API_TITLE = "Larva API Document";
    private static final String API_KEY_NAME = "FirebaseToken";

    private final AppInfo appInfo;

    @Bean
    public Docket currentApi() {
        final String api = appInfo.getApi();
        final String profile = appInfo.getProfile();

        boolean enabled = (profile.equals("dev") || profile.equals("local"));

        return baseApi(api)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.ant(api + "/**"))
                .build()
                .enable(enabled);
    }

    private Docket baseApi(String groupName) {
        return new Docket(DocumentationType.SWAGGER_2)
                .useDefaultResponseMessages(false)
                .globalRequestParameters(globalRequestParameters())
                .securityContexts(securityContexts())
                .securitySchemes(securitySchemes())
                .apiInfo(apiInfo())
                .groupName(groupName);
    }

    private List<RequestParameter> globalRequestParameters() {
        List<RequestParameter> params = new ArrayList<>();
        params.add(new RequestParameterBuilder()
                .name(LarvaConst.X_ACCESS_TOKEN)
                .description("Access Token")
                .in(ParameterType.HEADER)
                .query(q -> q.model(m -> m.scalarModel(ScalarType.STRING)))
                .required(false)
                .build());
        return params;
    }

    private List<SecurityContext> securityContexts() {
        return Collections.singletonList(SecurityContext.builder()
                .securityReferences(securityReferences())
                .operationSelector(context -> true)
                .build());
    }

    private List<SecurityReference> securityReferences() {
        AuthorizationScope[] authorizationScopes = {
                new AuthorizationScopeBuilder()
                        .scope("global")
                        .build()
        };
        return Collections.singletonList(new SecurityReference(API_KEY_NAME, authorizationScopes));
    }

    private List<SecurityScheme> securitySchemes() {
        return Collections.singletonList(new ApiKey(API_KEY_NAME, LarvaConst.X_ACCESS_TOKEN, "header"));
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title(API_TITLE)
                .license(appInfo.getLicense())
                .version(appInfo.getVersion())
                .build();
    }
}

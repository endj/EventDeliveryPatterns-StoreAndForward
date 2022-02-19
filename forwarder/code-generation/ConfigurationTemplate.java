public class ConfigurationTemplate {

    private static final String TEMPLATE = """
            package @{basepackage}@.config;
                        
            import @{basepackage}@.adapters.Stub@{className}@Repository;
            import @{basepackage}@.core.ServiceImplementation;
            import @{basepackage}@.core.ports.@{className}@Repository;
            import @{basepackage}@.core.ports.@{className}@Service;
            import org.springframework.context.annotation.Bean;
            import org.springframework.context.annotation.Configuration;
                        
                        
            @Configuration
            public class Config {

                @Bean
                public @{className}@Repository @{classNameLower}@Repository() {
                    return new Stub@{className}@Repository();
                }

                @Bean
                public @{className}@Service @{classNameLower}@Service(@{className}@Repository @{classNameLower}@Repository) {
                    return new ServiceImplementation(@{classNameLower}@Repository);
                }

            }
            """;

    private static final String YAML_TEMPLATE = """
            spring:
              application:
                name: @{serviceName}@
            """;

    private static final String LOMBOK_CONFIG = """
            lombok.anyConstructor.addConstructorProperties=true
            """;

    public static String createLombokConfig() {
        return LOMBOK_CONFIG;
    }
    public static String createYaml(String serviceName) {
        return YAML_TEMPLATE.replace(TemplateVariables.SERVICE_NAME_TEMPLATE, serviceName);
    }

    public static String createConfigurationFile(String className) {
        return TEMPLATE.replace(TemplateVariables.CLASS_TEMPLATE, className)
                .replace(TemplateVariables.CLASS_LOWER_TEMPLATE, TextTransformer.firstLowerCase(className))
                .replace(TemplateVariables.BASE_PACKAGE_PATH_TEMPLATE, FileManager.BASE_IMPORT_PACKAGE);
    }

}

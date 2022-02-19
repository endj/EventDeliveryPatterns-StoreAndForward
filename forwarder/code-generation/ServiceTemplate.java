public class ServiceTemplate {


    private static final String TEMPLATE = """
            package @{basepackage}@.core;
                        
            import @{basepackage}@.core.ports.@{className}@Repository;
            import @{basepackage}@.core.ports.@{className}@Service;
                        
            import java.util.List;
            import java.util.UUID;
                        
            public class ServiceImplementation implements @{className}@Service {
               \s
                private final @{className}@Repository @{classNameLower}@Repository;
                        
                public ServiceImplementation(@{className}@Repository @{classNameLower}@Repository) {
                    this.@{classNameLower}@Repository = @{classNameLower}@Repository;
                }
                        
                @Override
                public @{className}@ find(UUID id) {
                    return @{classNameLower}@Repository.find(id);
                }
                        
                @Override
                public List<UUID> list() {
                    return @{classNameLower}@Repository.list();
                }
                        
                @Override
                public @{className}@ create(@{className}@ data) {
                    return @{classNameLower}@Repository.save(data);
                }
            }
                        
            """;

    private static final String MAIN_TEMPLATE = """
            package @{basepackage}@;
                        
                        
            import org.springframework.boot.SpringApplication;
            import org.springframework.boot.autoconfigure.SpringBootApplication;
                        
            @SpringBootApplication
            public class @{serviceName}@Application {
                public static void main(String[] args) {
                    SpringApplication.run(@{serviceName}@Application.class, args);
                }
            }
                        
            """;

    public static String createMain(String serviceName) {
        return MAIN_TEMPLATE.replace(TemplateVariables.SERVICE_NAME_TEMPLATE, TextTransformer.firstUpperCase(serviceName).replace("-", ""))
                .replace(TemplateVariables.BASE_PACKAGE_PATH_TEMPLATE, FileManager.BASE_IMPORT_PACKAGE);
    }

    public static String createDefaultServiceImplementation(String className) {
        return TEMPLATE.replace(TemplateVariables.CLASS_TEMPLATE, className)
                .replace(TemplateVariables.CLASS_LOWER_TEMPLATE, TextTransformer.firstLowerCase(className))
                .replace(TemplateVariables.BASE_PACKAGE_PATH_TEMPLATE, FileManager.BASE_IMPORT_PACKAGE);
    }
}

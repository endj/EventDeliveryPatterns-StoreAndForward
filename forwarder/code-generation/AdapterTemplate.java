public class AdapterTemplate {


    private static final String OUT_TEMPLATE = """
            package @{basepackage}@.adapters;
                        
            import @{basepackage}@.core.ports.@{className}@Repository;
            import @{basepackage}@.core.@{className}@;
                        
            import java.util.List;
            import java.util.UUID;
                        
            public class Stub@{className}@Repository implements @{className}@Repository {
                @Override
                public @{className}@ save(@{className}@ @{classNameLower}@) {
                    return @{classNameLower}@;
                }
                        
                @Override
                public List<UUID> list() {
                    return List.of();
                }
                        
                @Override
                public @{className}@ find(UUID id) {
                    return null;
                }
            }
            """;

    private static final String CONTROLLER_TEMPLATE = """
            package @{basepackage}@.adapters;
                        
            import @{basepackage}@.core.ports.@{className}@Service;
            import @{basepackage}@.core.@{className}@;
            import lombok.extern.slf4j.Slf4j;
            import org.springframework.web.bind.annotation.*;
                        
            import java.util.List;
            import java.util.UUID;
                        
            @Slf4j
            @RequestMapping("/@{serviceName}@/@{classNameLower}@")
            @RestController
            public class @{className}@Controller  {
                        
                private final @{className}@Service @{classNameLower}@Service;
                        
                public @{className}@Controller(@{className}@Service @{classNameLower}@Service) {
                    this.@{classNameLower}@Service = @{classNameLower}@Service;
                }
                        
                @GetMapping("/{id}")
                public @{className}@ find(@PathVariable UUID id) {
                    return @{classNameLower}@Service.find(id);
                }
                        
                @GetMapping
                public List<UUID> list() {
                    return @{classNameLower}@Service.list();
                }
                        
                @PostMapping
                public @{className}@ create(@RequestBody @{className}@ @{classNameLower}@) {
                    log.info("Got request {}", @{classNameLower}@);
                    return @{classNameLower}@Service.create(@{classNameLower}@);
                }
            }
                        
            """;

    public static String createInboundImplementation(String className, String serviceName) {
        return CONTROLLER_TEMPLATE.replace(TemplateVariables.CLASS_TEMPLATE, className)
                .replace(TemplateVariables.CLASS_LOWER_TEMPLATE, TextTransformer.firstLowerCase(className))
                .replace(TemplateVariables.SERVICE_NAME_TEMPLATE, serviceName)
                .replace(TemplateVariables.BASE_PACKAGE_PATH_TEMPLATE, FileManager.BASE_IMPORT_PACKAGE);
    }

    public static String createOutboundImplementation(String className) {
        return OUT_TEMPLATE.replace(TemplateVariables.CLASS_TEMPLATE, className)
                .replace(TemplateVariables.CLASS_LOWER_TEMPLATE, TextTransformer.firstLowerCase(className))
                        .replace(TemplateVariables.BASE_PACKAGE_PATH_TEMPLATE, FileManager.BASE_IMPORT_PACKAGE);

    }
}

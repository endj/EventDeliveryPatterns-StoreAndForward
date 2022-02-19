public class InterfaceTemplate {

    private static final String IN_TEMPLATE = """
            package @{basepackage}@.core.ports;
                        
            import @{basepackage}@.core.@{className}@;
                        
            import java.util.List;
            import java.util.UUID;
                        
            public interface @{className}@Service {
                        
                @{className}@ find(UUID id);
                
                List<UUID> list();
                
                @{className}@ create(@{className}@ data);
            }
                        
            """;

    private static final String OUT_TEMPLATE = """
            package @{basepackage}@.core.ports;
            
            import @{basepackage}@.core.@{className}@;
                        
            import java.util.List;
            import java.util.UUID;
                        
            public interface @{className}@Repository {

                @{className}@ save(@{className}@ data);

                List<UUID> list();

                @{className}@ find(UUID id);
            }
            """;

    public static String createInbound(String className) {
        return IN_TEMPLATE.replace(TemplateVariables.CLASS_TEMPLATE, className)
                .replace(TemplateVariables.BASE_PACKAGE_PATH_TEMPLATE, FileManager.BASE_IMPORT_PACKAGE);
    }


    public static String createOutbound(String className) {
        return OUT_TEMPLATE.replace(TemplateVariables.CLASS_TEMPLATE, className)
                .replace(TemplateVariables.BASE_PACKAGE_PATH_TEMPLATE, FileManager.BASE_IMPORT_PACKAGE);

    }
}

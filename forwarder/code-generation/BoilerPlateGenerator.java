public class BoilerPlateGenerator {
    private static final String DEFAULT_MODEL_JSON_FILE_PATH = "defaultModel.json";

    public static void main(String[] args) throws Exception {
        SanityCheck.preRequirementCheck();

        var config = Configuration.parseArguments(args);
        var className = domainObjectClassName(config);

        createFolderStructure(config);
        createPomFile(config);
        createYamlFile(config);
        createLombokConfigFile();
        createDomainObjects(config, className);
        createInterfaces(className);
        createService(className);
        createAdapters(className, config.serviceName());
        createSpringConfiguration(className);
        createMain(config.serviceName());
        createIntegrationTest(config, className);
    }

    private static void createIntegrationTest(Configuration config, String className) throws Exception {
        var testCode = TestTemplate.generateIntegrationTestCode(config,
                FileManager.readFileContent(config.getJsonDefinitionFile().orElse(DEFAULT_MODEL_JSON_FILE_PATH)),
                className);
        FileManager.writeFile(testCode, FileManager.JAVA_TEST_BASE_PATH + "/IntegrationTest.java");
    }

    private static void createLombokConfigFile() {
        var lombokConfig = ConfigurationTemplate.createLombokConfig();
        FileManager.writeFile(lombokConfig,
                "../lombok.config");
    }

    private static void createMain(String serviceName) {
        var mainCode = ServiceTemplate.createMain(serviceName);
        FileManager.writeFile(mainCode,
                FileManager.JAVA_BASE_PATH + "/" + TextTransformer.firstUpperCase(serviceName).replace("-", "") + "Application.java");
    }

    private static void createService(String className) {
        var serviceCode = ServiceTemplate.createDefaultServiceImplementation(className);
        FileManager.writeFile(serviceCode, FileManager.JAVA_CORE_PACKAGE_PATH + "/ServiceImplementation.java");
    }

    public static void createSpringConfiguration(String className) {
        var configurationCode = ConfigurationTemplate.createConfigurationFile(className);
        FileManager.writeFile(configurationCode, FileManager.JAVA_CONFIGURATION_PACKAGE_PATH + "/Config.java");
    }

    private static void createAdapters(String className, String serviceName) {
        var interfaceCodeIn = AdapterTemplate.createInboundImplementation(className, serviceName);
        FileManager.writeFile(interfaceCodeIn, FileManager.JAVA_ADAPTER_PACKAGE_PATH + "/" +  className + "Controller.java");
        var interfaceCodeOut = AdapterTemplate.createOutboundImplementation(className);
        FileManager.writeFile(interfaceCodeOut, FileManager.JAVA_ADAPTER_PACKAGE_PATH + "/Stub" +className + "Repository.java");
    }

    private static void createInterfaces(String className) {
        var inData = InterfaceTemplate.createInbound(className);
        FileManager.writeFile(inData, FileManager.JAVA_CORE_PACKAGE_PATH + "/ports/" + className + "Service.java");
        var outData = InterfaceTemplate.createOutbound(className);
        FileManager.writeFile(outData, FileManager.JAVA_CORE_PACKAGE_PATH + "/ports/" + className + "Repository.java");
    }

    private static String domainObjectClassName(Configuration config) {
        var domainDefinitionFilePath = config.getJsonDefinitionFile().orElse(DEFAULT_MODEL_JSON_FILE_PATH);
        return TextTransformer.classNameFromJsonFile(domainDefinitionFilePath);
    }

    private static void createYamlFile(Configuration configuration) {
        var yaml = ConfigurationTemplate.createYaml(configuration.serviceName());
        FileManager.writeFile(yaml, "../src/main/resources/application.yaml");
    }

    private static void createPomFile(Configuration config) {
        var pomContent = PomGenerator.builder()
                .withArtifactId(config.artifactId())
                .withGroupId(config.groupId())
                .withServiceName(config.serviceName())
                .withSpringBootParentVersion("2.6.3")
                .build();
        FileManager.writeFile(pomContent, "../pom.xml");
    }

    private static void createDomainObjects(Configuration config, String className) throws Exception {
        var domainDefinitionFile = config.getJsonDefinitionFile().orElse(DEFAULT_MODEL_JSON_FILE_PATH);
        var node = ModelParser.parseDomainModel(FileManager.readFileContent(domainDefinitionFile));
        SourceCodeGenerator.generateDomainObject(node, className);
    }

    private static void createFolderStructure(Configuration configuration) {
        FileManager.createPackage(configuration);
        FileManager.createHexagonialFolderStructure();
    }


}

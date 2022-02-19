import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileManager {
    private static final String FOLDER_ERROR_F = "Failed to create folder %s , aborting..";
    private static final String FILE_ERROR_F = "Failed to create file %s , aborting..";
    private static final String MISSING_FILE_ERROR_F = "No such file %s , aborting..";

    public static String BASE_IMPORT_PACKAGE;
    public static String RESOURCES_PATH = "../src/main/resources";
    public static String JAVA_BASE_PATH = "../src/main/java";
    public static String JAVA_BASE_TEST_PATH = "../src/test/java";
    public static String JAVA_CORE_PACKAGE_PATH;
    public static String JAVA_ADAPTER_PACKAGE_PATH;
    public static String JAVA_CONFIGURATION_PACKAGE_PATH;
    public static String JAVA_TEST_BASE_PATH;

    public static void createHexagonialFolderStructure() {
        createJavaFolder("/config");
        createJavaFolder("/core");
        createJavaFolder("/adapters");
        createJavaFolder("/core/ports");
    }

    private static void createResourceFile(String fileName) throws IOException {
        var basePath = "../src/main/resources";
        var file = new File(basePath + fileName);
        if (!file.createNewFile())
            throw new RuntimeException(FILE_ERROR_F.formatted(basePath + fileName));
    }

    private static void createJavaFolder(String folderPath) {
        var folder = new File(JAVA_BASE_PATH + folderPath);
        if (!folder.mkdir())
            throw new RuntimeException(FOLDER_ERROR_F.formatted(JAVA_BASE_PATH + folderPath));
    }

    public static String readFileContent(String filePath) throws Exception {
        if (!Files.exists(Path.of(filePath)))
            throw new RuntimeException(MISSING_FILE_ERROR_F.formatted(filePath));
        return Files.readString(Path.of(filePath));
    }


    public static void writeFile(String content, String filePath) {
        try {
            System.out.println("Creating file " + filePath);
            var path = Path.of(filePath);
            var write = Files.writeString(path, content);
        } catch (Exception e) {
            throw new RuntimeException(FILE_ERROR_F.formatted(filePath), e);
        }
    }

    public static void createPackage(Configuration configuration) {
        var groupFolderStructure = configuration.groupId().replace(".", "/");
        File defaultPackage = new File(JAVA_BASE_PATH + "/" + groupFolderStructure);
        if (!defaultPackage.mkdirs())
            throw new RuntimeException(FOLDER_ERROR_F.formatted(defaultPackage.getName()));

        File resources = new File(RESOURCES_PATH);
        if(!resources.mkdirs())
            throw new RuntimeException(FOLDER_ERROR_F.formatted(resources.getName()));

        File testPackage = new File(JAVA_BASE_TEST_PATH + "/" + groupFolderStructure);
        if(!testPackage.mkdirs())
            throw new RuntimeException(FOLDER_ERROR_F.formatted(testPackage.getName()));

        BASE_IMPORT_PACKAGE = configuration.groupId();
        JAVA_BASE_PATH = JAVA_BASE_PATH + "/" + groupFolderStructure;
        JAVA_CORE_PACKAGE_PATH = JAVA_BASE_PATH + "/core";
        JAVA_ADAPTER_PACKAGE_PATH = JAVA_BASE_PATH + "/adapters";
        JAVA_CONFIGURATION_PACKAGE_PATH = JAVA_BASE_PATH + "/config";
        JAVA_TEST_BASE_PATH = "../src/test/java/" + groupFolderStructure;
    }
}

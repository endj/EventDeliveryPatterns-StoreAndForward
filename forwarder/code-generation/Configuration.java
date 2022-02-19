import java.util.Arrays;
import java.util.Optional;

public class Configuration {
    private final String groupId;
    private final String artifactId;
    private final String serviceName;
    private final String jsonDefinitionFile;


    public static Configuration parseArguments(String[] args) {
        return new Configuration(args);
    }

    private Configuration(String[] args) {
        System.out.println(Arrays.toString(args));
        this.groupId = requiredArg(args, "groupId");
        this.artifactId = requiredArg(args, "artifactId");
        this.serviceName = requiredArg(args, "serviceName");
        this.jsonDefinitionFile = optionalArg(args, "domainJsonDefinitionFile").orElse(null);
    }

    public String groupId() {
        return groupId;
    }

    public String artifactId() {
        return artifactId;
    }

    public String serviceName() {
        return serviceName;
    }


    public Optional<String> getJsonDefinitionFile() {
        return Optional.ofNullable(jsonDefinitionFile);
    }

    public Optional<String> optionalArg(String[] args, String key) {
        return Arrays.stream(args)
                .filter(this::validFormat)
                .filter(arg -> arg.contains(key))
                .findAny()
                .map(arg -> arg.split("=")[1]);
    }

    public String requiredArg(String[] args, String key) {
        return Arrays.stream(args)
                .filter(this::validFormat)
                .filter(arg -> arg.contains(key))
                .findAny()
                .map(arg -> arg.split("=")[1])
                .orElseThrow(() -> new RuntimeException("Could not find argument with key " + key));
    }

    private boolean validFormat(String arg) {
        return arg.contains("=") && arg.split("=").length == 2;
    }

}

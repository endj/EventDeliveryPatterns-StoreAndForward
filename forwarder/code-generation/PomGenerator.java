import java.util.Objects;

public class PomGenerator {

    private static final String GROUP_ID = "group_id";
    private static final String ARTIFACT_ID = "artifact_id";
    private static final String SERVICE_NAME = "service_name";
    private static final String SPRING_BOOT_PARENT_VERSION = "spring_boot_starter_version";

    private static final String TEMPLATE = """
            <?xml version="1.0" encoding="UTF-8"?>
            <project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
            	xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
            	<modelVersion>4.0.0</modelVersion>
            	
            	<parent>
            		<groupId>org.springframework.boot</groupId>
            		<artifactId>spring-boot-starter-parent</artifactId>
            		<version>@{spring_boot_starter_version}@</version>
            		<relativePath/> <!-- lookup parent from repository -->
            	</parent>
            	
            	<groupId>@{group_id}@</groupId>
            	<artifactId>@{artifact_id}@</artifactId>
            	<version>0.0.1-SNAPSHOT</version>
            	
            	<name>@{service_name}@</name>
            	<description>@{service_name}@</description>
            	
            	<properties>
            		<java.version>17</java.version>
            	</properties>
                        
            	<dependencies>
            		<dependency>
                    	<groupId>org.springframework.boot</groupId>
                    	<artifactId>spring-boot-starter-web</artifactId>
                    </dependency>
            		<dependency>
            			<groupId>org.springframework.boot</groupId>
            			<artifactId>spring-boot-starter-test</artifactId>
            			<scope>test</scope>
            		</dependency>
            		<dependency>
            			<groupId>org.projectlombok</groupId>
            			<artifactId>lombok</artifactId>
            		</dependency>
            	</dependencies>
                        
            	<build>
            		<plugins>
            			<plugin>
            				<groupId>org.springframework.boot</groupId>
            				<artifactId>spring-boot-maven-plugin</artifactId>
            			</plugin>
            		</plugins>
            	</build>
                        
            </project>
            """;

    public static PomBuilder builder() {
        return new PomBuilder();
    }

    public static class PomBuilder {
        String groupId;
        String artifactId;
        String serviceName;
        String springBootVersion;
        String copy = TEMPLATE;

        public PomBuilder withGroupId(String groupId) {
            this.groupId = groupId;
            return this;
        }

        public PomBuilder withArtifactId(String artifactId) {
            this.artifactId = artifactId;
            return this;
        }

        public PomBuilder withServiceName(String serviceName) {
            this.serviceName = serviceName;
            return this;
        }

        public PomBuilder withSpringBootParentVersion(String springBootVersion) {
            this.springBootVersion = springBootVersion;
            return this;
        }

        public String build() {
            Objects.requireNonNull(groupId);
            Objects.requireNonNull(artifactId);
            Objects.requireNonNull(serviceName);
            Objects.requireNonNull(springBootVersion);
            setValue(GROUP_ID, groupId);
            setValue(ARTIFACT_ID, artifactId);
            setValue(SERVICE_NAME, serviceName);
            setValue(SPRING_BOOT_PARENT_VERSION, springBootVersion);
            return copy;
        }

        public void setValue(String placeHolder, String value) {
            copy = copy.replace("@{%s}@".formatted(placeHolder), value);
        }

    }
}

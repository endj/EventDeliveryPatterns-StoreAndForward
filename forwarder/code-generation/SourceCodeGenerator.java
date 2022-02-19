import java.util.List;
import java.util.Map;

public class SourceCodeGenerator {


    public static void generateDomainObject(Object node, String className) {
        var content = generateDomainObjectInternal(node, className);
        FileManager.writeFile(content, FileManager.JAVA_CORE_PACKAGE_PATH + "/%s.java"
                .formatted(TextTransformer.firstUpperCase(className)));
    }

    @SuppressWarnings("unchecked")
    public static String generateDomainObjectInternal(Object node, String className) {
        ClassBuilder builder = builder(className);

        if (node instanceof String s) {
            return builder.addField(TextTransformer.firstUpperCase(s), s)
                    .build();
        }

        if (node instanceof List l) {
            builder.addImport("import java.util.List;");
            Object listItem = l.get(0);

            if (listItem instanceof String s) {
                builder.addField("List<%s>".formatted(TextTransformer.firstUpperCase(s)), s);
            }

            if (listItem instanceof Map m) {
                var listItemName = className + "ListItem";
                builder.addField("List<%s>".formatted(TextTransformer.firstUpperCase(listItemName)), listItemName);
                var content = generateDomainObjectInternal(l.get(0), listItemName);
                FileManager.writeFile(content,
                        FileManager.JAVA_CORE_PACKAGE_PATH + "/%s.java"
                                .formatted(TextTransformer.firstUpperCase(listItemName)));
            }
        }

        if (node instanceof Map root) {
            var iterator = root.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, ?> next = (Map.Entry<String, ?>) iterator.next();

                if (next.getValue() instanceof String s)
                    builder.addField(s, next.getKey());

                if (next.getValue() instanceof Map m) {
                    builder.addField(next.getKey(), next.getKey());
                    var content = generateDomainObjectInternal(m, next.getKey());
                    FileManager.writeFile(content,
                            FileManager.JAVA_CORE_PACKAGE_PATH + "/%s.java"
                                    .formatted(TextTransformer.firstUpperCase(next.getKey())));
                }
                if (next.getValue() instanceof List l) {
                    builder.addImport("import java.util.List;");
                    var listItem = l.get(0);
                    if (listItem instanceof String s) {
                        builder.addField("List<%s>".formatted(TextTransformer.firstUpperCase(s)), next.getKey());
                    }

                    if (listItem instanceof Map m) {
                        var listItemName = next.getKey() + "ListItem";
                        builder.addField("List<%s>".formatted(TextTransformer.firstUpperCase(listItemName)), next.getKey());
                            var content = generateDomainObjectInternal(m, listItemName);
                            FileManager.writeFile(content,
                                    FileManager.JAVA_CORE_PACKAGE_PATH + "/%s.java"
                                            .formatted(TextTransformer.firstUpperCase(listItemName)));
                    }
                }
            }
        }
        return builder.build();
    }

    public static ClassBuilder builder(String className) {
        return new ClassBuilder(className);
    }

    public static class ClassBuilder {

        private final StringBuilder body;
        private final StringBuilder imports;
        private final StringBuilder packageLine = new StringBuilder("package %s.core;\n\n".formatted(FileManager.BASE_IMPORT_PACKAGE));

        public ClassBuilder(String className) {
            this.body = new StringBuilder("""
                    @Value
                    @Builder(toBuilder = true)
                    @AllArgsConstructor
                    public class %s {
                    """.formatted(TextTransformer.firstUpperCase(className)));
            this.imports = new StringBuilder("import lombok.Value;\nimport lombok.Builder;\nimport lombok.AllArgsConstructor;\n");
        }


        public ClassBuilder addField(String type, String variableName) {
            body.append("""
                        %s %s;
                    """.formatted(TextTransformer.firstUpperCase(type), variableName));
            return this;
        }

        public String build() {

            return packageLine.append(imports)
                    .append("\n\n")
                    .append(body)
                    .append("}").toString();
        }

        public void addImport(String s) {
            imports.append(s);
        }
    }


}

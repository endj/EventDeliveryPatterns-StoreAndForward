public class SanityCheck {
    private static final String WRONG_FOLDER_F = "Invoked from incorrect folder, called from %s expected parent folder to be code-generation";

    public static void preRequirementCheck() {
        correctInvocationFolder();
    }

    public static void correctInvocationFolder() {
        var folder = System.getProperty("user.dir");
        if (!System.getProperty("user.dir").endsWith("code-generation"))
            throw new RuntimeException(WRONG_FOLDER_F.formatted(folder));
    }

}

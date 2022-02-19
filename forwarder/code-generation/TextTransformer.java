public  class TextTransformer {

    static String firstUpperCase(String s) {
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    static String classNameFromJsonFile(String filePath) {
        int i = filePath.indexOf(".json");
        if (i == -1)
            throw new RuntimeException("Cant parse classname from " + filePath);
        var sb = new StringBuilder();
        while (i-- > 0) {
            var c = filePath.charAt(i);
            if (c == '.' || c == '/')
                break;
            sb.append(c);
        }
        sb.reverse();
        sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
        return sb.toString();
    }

    public static CharSequence firstLowerCase(String s) {
        return Character.toLowerCase(s.charAt(0)) + s.substring(1);
    }

}

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class ModelParser {

    @SuppressWarnings("unchecked")
    public static Object parseDomainModel(String data) {

        var fieldMap = new HashMap<>();
        var tokens = data.toCharArray();
        var index = 0;
        var sb = new StringBuilder();
        var stack = new ArrayDeque<ParseTokens>();

        var lastWasField = false;
        String lastField = null;

        while (index != tokens.length - 1) {

            var token = tokens[index++];
            if (Character.isWhitespace(token) || token == ',' || token == ':')
                continue;

            switch (token) {
                case '{':
                    if (lastWasField) {
                        var subObjectRange = rangeBracket(data, index);
                        Object node = parseDomainModel(data.substring(subObjectRange[0], subObjectRange[1]));
                        Object fieldVal = fieldMap.get(lastField);
                        if (fieldVal instanceof List l) {
                            l.add(node);
                        } else {
                            fieldMap.put(lastField, node);
                        }
                        index += subObjectRange[1] - subObjectRange[0];
                        lastWasField = false;
                    }
                    break;
                case '"':
                    var last = stack.peek();
                    if (last == ParseTokens.DOUBLE_QUOTE) {
                        stack.pop();
                        if (lastWasField) {

                            var field = sb.toString();
                            Object fieldVal = fieldMap.get(lastField);
                            if (fieldVal instanceof List l) {
                                l.add(field);
                            } else {
                                fieldMap.put(lastField, field);
                            }
                            sb = new StringBuilder();
                            lastField = null;
                            lastWasField = false;
                        } else {
                            fieldMap.put(sb.toString(), null);
                            lastField = sb.toString();
                            sb = new StringBuilder();
                            lastWasField = true;
                        }
                    } else {
                        stack.push(ParseTokens.DOUBLE_QUOTE);
                    }
                    break;
                case '[':
                    if (lastWasField)
                        fieldMap.put(lastField, new ArrayList<>());
                    stack.push(ParseTokens.OPEN_LIST);
                    break;
                case ']':
                    if (stack.pop() != ParseTokens.OPEN_LIST)
                        throw new RuntimeException("Invalid JSON");
                    break;
                default:
                    sb.append(token);
            }

        }
        return fieldMap;
    }

    static int[] rangeBracket(String data, int fromIndex) {
        var queue = new ArrayDeque<ParseTokens>();
        queue.push(ParseTokens.OPEN_BRACKET);
        for (int i = fromIndex + 1; i < data.length(); i++) {
            var c = data.charAt(i);
            if (c == '{')
                queue.push(ParseTokens.OPEN_BRACKET);
            if (c == '}')
                queue.pop();
            if (queue.isEmpty())
                return new int[]{fromIndex - 1, i + 1};
        }
        throw new IllegalStateException();
    }

    enum ParseTokens {
        OPEN_BRACKET,
        CLOSED_BRACKET,
        DOUBLE_QUOTE,
        OPEN_LIST,
        CLOSE_LIST
    }


}

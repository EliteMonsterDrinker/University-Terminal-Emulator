import java.util.*;

public final class Json {
    private final String src;
    private int pos;

    private static final Map<Character, Character> ESCAPES = Map.of(
        '"',  '"',
        '\\', '\\',
        '/',  '/',
        'b',  '\b',
        'f',  '\f',
        'n',  '\n',
        'r',  '\r',
        't',  '\t'
    );

    private Json(String src) { this.src = src; }

    public static Object parse(String s) {
        Json p = new Json(s);
        p.skipWs();
        Object v = p.parseValue();
        p.skipWs();
        p.requireEnd();
        return v;
    }


    private void skipWs() {
        while (pos < src.length() && Character.isWhitespace(src.charAt(pos))) pos++;
    }

    private char peek() {
        if (pos >= src.length()) throw new IllegalArgumentException("Неожиданный конец JSON");
        return src.charAt(pos);
    }

    private void expect(char c) {
        if (peek() != c) throw new IllegalArgumentException(
            "Ожидалось '" + c + "' в позиции " + pos);
        pos++;
    }

    private void expect(String s) {
        if (!src.startsWith(s, pos)) throw new IllegalArgumentException("Ожидалось " + s);
        pos += s.length();
    }

    private void requireEnd() {
        if (pos < src.length()) {
            throw new IllegalArgumentException("Лишние символы после JSON: " + pos);
        }
    }


    private Object parseValue() {
        skipWs();
        switch (peek()) {
            case '{': return parseObject();
            case '[': return parseArray();
            case '"': return parseString();
            case 't': expect("true");  return Boolean.TRUE;
            case 'f': expect("false"); return Boolean.FALSE;
            case 'n': expect("null");  return null;
            default:  return parseNumber();
        }
    }


    private Map<String, Object> parseObject() {
        Map<String, Object> map = new LinkedHashMap<>();
        pos++;
        skipWs();
        if (peek() == '}') { pos++; return map; }

        while (true) {
            skipWs();
            String key = parseString();
            skipWs();
            expect(':');
            map.put(key, parseValue());
            skipWs();
            if (consumeSeparator('}', '}')) return map;
        }
    }

    private List<Object> parseArray() {
        List<Object> list = new ArrayList<>();
        pos++;
        skipWs();
        if (peek() == ']') { pos++; return list; }

        while (true) {
            list.add(parseValue());
            skipWs();
            if (consumeSeparator(']', ']')) return list;
        }
    }

    private boolean consumeSeparator(char close, char context) {
        char c = peek();
        if (c == close) { pos++; return true; }
        if (c == ',')   { pos++; return false; }
        throw new IllegalArgumentException(
            "Ожидалось ',' или '" + close + "' в позиции " + pos);
    }


    private String parseString() {
        expect('"');
        StringBuilder sb = new StringBuilder();
        while (pos < src.length()) {
            char c = src.charAt(pos++);
            if (c == '"') return sb.toString();
            if (c == '\\') sb.append(readEscape());
            else           sb.append(c);
        }
        throw new IllegalArgumentException("Незакрытая строка");
    }

    private char readEscape() {
        char e = peek();
        pos++;
        Character simple = ESCAPES.get(e);
        if (simple != null) return simple;
        if (e == 'u')       return readUnicodeEscape();
        throw new IllegalArgumentException("Недопустимая escape-последовательность");
    }

    private char readUnicodeEscape() {
        if (pos + 4 > src.length()) throw new IllegalArgumentException("Обрезанный \\u escape");
        String hex = src.substring(pos, pos + 4);
        pos += 4;
        try {
            return (char) Integer.parseInt(hex, 16);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Неверный \\u escape: " + hex);
        }
    }


    private Object parseNumber() {
        int start = pos;
        if (peek() == '-') pos++;
        while (pos < src.length() && isNumberChar(src.charAt(pos))) pos++;
        String num = src.substring(start, pos);
        if (num.contains(".") || num.contains("e") || num.contains("E")) return Double.parseDouble(num);
        return Long.parseLong(num);
    }

    private static boolean isNumberChar(char c) {
        return Character.isDigit(c) || c == '.' || c == 'e' || c == 'E' || c == '+' || c == '-';
    }
}

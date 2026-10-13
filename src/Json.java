import java.util.*;

public final class Json {
    private final String src;
    private int pos;

    private Json(String src) { this.src = src; }

    public static Object parse(String s) {
        Json p = new Json(s);
        p.skipWs();
        Object v = p.parseValue();
        p.skipWs();
        if (p.pos < p.src.length()) {
            throw new IllegalArgumentException("Лишние символы после JSON: " + p.pos);
        }
        return v;
    }

    private void skipWs() {
        while (pos < src.length() && Character.isWhitespace(src.charAt(pos))) pos++;
    }

    private Object parseValue() {
        skipWs();
        if (pos >= src.length()) throw new IllegalArgumentException("Неожиданный конец JSON");
        char c = src.charAt(pos);
        switch (c) {
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
        if (pos < src.length() && src.charAt(pos) == '}') { pos++; return map; }
        while (true) {
            skipWs();
            String key = parseString();
            skipWs();
            if (src.charAt(pos) != ':') throw new IllegalArgumentException("Ожидалось ':' в позиции " + pos);
            pos++;
            Object val = parseValue();
            map.put(key, val);
            skipWs();
            char c = src.charAt(pos);
            if (c == ',') { pos++; continue; }
            if (c == '}') { pos++; break; }
            throw new IllegalArgumentException("Ожидалось ',' или '}' в позиции " + pos);
        }
        return map;
    }

    private List<Object> parseArray() {
        List<Object> list = new ArrayList<>();
        pos++;
        skipWs();
        if (pos < src.length() && src.charAt(pos) == ']') { pos++; return list; }
        while (true) {
            list.add(parseValue());
            skipWs();
            char c = src.charAt(pos);
            if (c == ',') { pos++; continue; }
            if (c == ']') { pos++; break; }
            throw new IllegalArgumentException("Ожидалось ',' или ']' в позиции " + pos);
        }
        return list;
    }

    private String parseString() {
        if (src.charAt(pos) != '"') throw new IllegalArgumentException("Ожидалась строка в позиции " + pos);
        pos++;
        StringBuilder sb = new StringBuilder();
        while (pos < src.length()) {
            char c = src.charAt(pos++);
            if (c == '"') return sb.toString();
            if (c == '\\') {
                char e = src.charAt(pos++);
                switch (e) {
                    case '"':  sb.append('"');  break;
                    case '\\': sb.append('\\'); break;
                    case '/':  sb.append('/');  break;
                    case 'b':  sb.append('\b'); break;
                    case 'f':  sb.append('\f'); break;
                    case 'n':  sb.append('\n'); break;
                    case 'r':  sb.append('\r'); break;
                    case 't':  sb.append('\t'); break;
                    case 'u':
                        String hex = src.substring(pos, pos + 4);
                        sb.append((char) Integer.parseInt(hex, 16));
                        pos += 4;
                        break;
                    default: throw new IllegalArgumentException("Недопустимая escape-последовательность");
                }
            } else {
                sb.append(c);
            }
        }
        throw new IllegalArgumentException("Незакрытая строка");
    }

    private Object parseNumber() {
        int start = pos;
        if (src.charAt(pos) == '-') pos++;
        while (pos < src.length()) {
            char c = src.charAt(pos);
            if (Character.isDigit(c) || c == '.' || c == 'e' || c == 'E' || c == '+' || c == '-') pos++;
            else break;
        }
        String num = src.substring(start, pos);
        if (num.contains(".") || num.contains("e") || num.contains("E")) return Double.parseDouble(num);
        return Long.parseLong(num);
    }

    private void expect(String s) {
        if (!src.startsWith(s, pos)) throw new IllegalArgumentException("Ожидалось " + s);
        pos += s.length();
    }
}

package lexical;

import java.util.HashMap;
import java.util.Map;

public class SymbolTable {

    private Map<String, TokenType> st;

    public SymbolTable() {
        st = new HashMap<String, TokenType>();

        // SYMBOLS
        st.put(";", TokenType.SEMI_COLON);
        st.put(":", TokenType.COLON);
        st.put(",", TokenType.COMMA);
        st.put(".", TokenType.DOT);
        st.put("(", TokenType.OPEN_PAR);
        st.put(")", TokenType.CLOSE_PAR);
        st.put("{", TokenType.OPEN_CUR);
        st.put("}", TokenType.CLOSE_CUR);
        st.put("[", TokenType.OPEN_BRA);
        st.put("]", TokenType.CLOSE_BRA);
        st.put("->", TokenType.ARROW);

        // OPERATORS
        st.put("=", TokenType.ASSIGN);
        st.put("+=", TokenType.ASSIGN_ADD);
        st.put("-=", TokenType.ASSIGN_SUB);
        st.put("*=", TokenType.ASSIGN_MUL);
        st.put("/=", TokenType.ASSIGN_DIV);
        st.put("%=", TokenType.ASSIGN_MOD);
        st.put("**=", TokenType.ASSIGN_POWER);
        st.put("&&", TokenType.AND);
        st.put("||", TokenType.OR);
        st.put("<", TokenType.LOWER);
        st.put(">", TokenType.GREATER);
        st.put("<=", TokenType.LOWER_EQUAL);
        st.put(">=", TokenType.GREATER_EQUAL);
        st.put("==", TokenType.EQUALS);
        st.put("!=", TokenType.NOT_EQUALS);
        st.put("in", TokenType.CONTAINS);
        st.put("!in", TokenType.NOT_CONTAINS);
        st.put("+", TokenType.ADD);
        st.put("-", TokenType.SUB);
        st.put("*", TokenType.MUL);
        st.put("/", TokenType.DIV);
        st.put("%", TokenType.MOD);
        st.put("**", TokenType.POWER);
        st.put("!", TokenType.NOT);
        st.put("as", TokenType.AS);

        // KEYWORDS
        st.put("def", TokenType.DEF);
        st.put("print", TokenType.PRINT);
        st.put("println", TokenType.PRINTLN);
        st.put("if", TokenType.IF);
        st.put("else", TokenType.ELSE);
        st.put("while", TokenType.WHILE);
        st.put("for", TokenType.FOR);
        st.put("foreach", TokenType.FOREACH);
        st.put("Boolean", TokenType.BOOLEAN);
        st.put("Integer", TokenType.INTEGER);
        st.put("String", TokenType.STRING);
        st.put("null", TokenType.NULL);
        st.put("false", TokenType.FALSE);
        st.put("true", TokenType.TRUE);
        st.put("read", TokenType.READ);
        st.put("empty", TokenType.EMPTY);
        st.put("size", TokenType.SIZE);
        st.put("keys", TokenType.KEYS);
        st.put("values", TokenType.VALUES);
        st.put("switch", TokenType.SWITCH);
        st.put("case", TokenType.CASE);
        st.put("default", TokenType.DEFAULT);
    }

    public boolean contains(String token) {
        return st.containsKey(token);
    }

    public TokenType find(String token) {
        return this.contains(token) ? st.get(token) : TokenType.NAME;
    }
}

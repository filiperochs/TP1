package lexical;

public enum TokenType {
    // SPECIALS
    UNEXPECTED_EOF,
    INVALID_TOKEN,
    END_OF_FILE,

    // SYMBOLS
    SEMI_COLON,    // ;
    COLON,         // :
    COMMA,         // ,
    DOT,           // .
    OPEN_PAR,      // (
    CLOSE_PAR,     // )
    OPEN_CUR,      // {
    CLOSE_CUR,     // }
    OPEN_BRA,      // [
    CLOSE_BRA,     // ]
    ARROW,         // ->

    // OPERATORS
    ASSIGN,        // =
    ASSIGN_ADD,    // +=
    ASSIGN_SUB,    // -=
    ASSIGN_MUL,    // *=
    ASSIGN_DIV,    // /=
    ASSIGN_MOD,    // %=
    ASSIGN_POWER,  // **=
    AND,           // &&
    OR,            // ||
    LOWER,         // <
    GREATER,       // >
    LOWER_EQUAL,   // <=
    GREATER_EQUAL, // >=
    EQUALS,        // ==
    NOT_EQUALS,    // !=
    CONTAINS,      // in
    NOT_CONTAINS,  // !in
    ADD,           // +
    SUB,           // -
    MUL,           // *
    DIV,           // /
    MOD,           // %
    POWER,         // **
    NOT,           // !
    AS,            // as

    // KEYWORDS
    DEF,           // def
    PRINT,         // print
    PRINTLN,       // println
    IF,            // if
    ELSE,          // else
    WHILE,         // while
    FOR,           // for
    FOREACH,       // foreach
    BOOLEAN,       // Boolean
    INTEGER,       // Integer
    STRING,        // String
    NULL,          // null
    FALSE,         // false
    TRUE,          // true
    READ,          // read
    EMPTY,         // empty
    SIZE,          // size
    KEYS,          // keys
    VALUES,        // values
    SWITCH,        // switch
    CASE,          // case
    DEFAULT,       // default

    // OTHERS
    NAME,          // identifier
    NUMBER,        // integer
    TEXT           // string

};

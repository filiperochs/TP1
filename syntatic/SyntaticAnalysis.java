package syntatic;

import interpreter.command.Command;
import lexical.Lexeme;
import lexical.LexicalAnalysis;
import lexical.TokenType;

import java.util.Stack;

public class SyntaticAnalysis {

    private LexicalAnalysis lex;
    private Lexeme current;
    private Stack<Lexeme> history;
    private Stack<Lexeme> queued;

    public SyntaticAnalysis(LexicalAnalysis lex) {
        this.lex = lex;
        this.current = lex.nextToken();
        this.history = new Stack<Lexeme>();
        this.queued = new Stack<Lexeme>();
    }

    public Command start() {
        return null;
    }

    private void rollback() {
        assert !history.isEmpty();

        // System.out.println("Rollback (\"" + current.token + "\", " +
        // current.type + ")");
        queued.push(current);
        current = history.pop();
    }

    private void advance() {
        // System.out.println("Advanced (\"" + current.token + "\", " +
        // current.type + ")");
        history.add(current);
        current = queued.isEmpty() ? lex.nextToken() : queued.pop();
    }

    private void eat(TokenType type) {
        // System.out.println("Expected (..., " + type + "), found (\"" +
        // current.token + "\", " + current.type + ")");
        if (type == current.type) {
            history.add(current);
            current = queued.isEmpty() ? lex.nextToken() : queued.pop();
        } else {
            showError();
        }
    }

    private void showError() {
        System.out.printf("%02d: ", lex.getLine());

        switch (current.type) {
            case INVALID_TOKEN:
                System.out.printf("Lexema inválido [%s]\n", current.token);
                break;
            case UNEXPECTED_EOF:
            case END_OF_FILE:
                System.out.printf("Fim de arquivo inesperado\n");
                break;
            default:
                System.out.printf("Lexema não esperado [%s]\n", current.token);
                break;
        }

        System.exit(1);
    }

    // <code> ::= { <cmd> }
    private void procCode() {
        while (current.type == TokenType.DEF ||
                current.type == TokenType.PRINT ||
                current.type == TokenType.PRINTLN ||
                current.type == TokenType.IF ||
                current.type == TokenType.WHILE ||
                current.type == TokenType.FOR ||
                current.type == TokenType.FOREACH ||
                current.type == TokenType.NOT ||
                current.type == TokenType.SUB ||
                current.type == TokenType.OPEN_PAR ||
                current.type == TokenType.NULL ||
                current.type == TokenType.FALSE ||
                current.type == TokenType.TRUE ||
                current.type == TokenType.NUMBER ||
                current.type == TokenType.TEXT ||
                current.type == TokenType.READ ||
                current.type == TokenType.EMPTY ||
                current.type == TokenType.SIZE ||
                current.type == TokenType.KEYS ||
                current.type == TokenType.VALUES ||
                current.type == TokenType.SWITCH ||
                current.type == TokenType.OPEN_BRA ||
                current.type == TokenType.NAME) {
            procCmd();
        }
    }

    // <cmd> ::= <decl> | <print> | <if> | <while> | <for> | <foreach> | <assign>
    private void procCmd() {
        switch (current.type) {
            case DEF:
                procDecl();
                break;
            case PRINT:
            case PRINTLN:
                procPrint();
                break;
            case IF:
                procIf();
                break;
            case WHILE:
                procWhile();
                break;
            case FOR:
                procFor();
                break;
            case FOREACH:
                procForeach();
                break;
            case NOT:
            case SUB:
            case OPEN_PAR:
            case NULL:
            case FALSE:
            case TRUE:
            case NUMBER:
            case TEXT:
            case READ:
            case EMPTY:
            case SIZE:
            case KEYS:
            case VALUES:
            case SWITCH:
            case OPEN_BRA:
            case NAME:
                procAssign();
                break;
            default:
                showError();
        }
    }

    // <decl> ::= def ( <decl-type1> | <decl-type2> )
    private void procDecl() {
        eat(TokenType.DEF);
        if (current.type == TokenType.NAME) {
            procDeclType1();
        } else {
            procDeclType2();
        }
    }

    // <decl-type1> ::= <name> [ '=' <expr> ] { ',' <name> [ '=' <expr> ] }
    private void procDeclType1() {
        advance();
        procName();

        if (current.type == TokenType.ASSIGN) {
            advance();
            procExpr();
        }

        while (current.type == TokenType.COMMA) {
            advance();
            procName();

            if (current.type == TokenType.ASSIGN) {
                advance();
                procExpr();
            }
        }
    }

    // <decl-type2> ::= '(' <name> { ',' <name> } ')' = <expr>
    private void procDeclType2() {
        eat(TokenType.OPEN_PAR);
        procName();

        while (current.type == TokenType.COMMA) {
            advance();
            procName();
        }

        eat(TokenType.CLOSE_PAR);
        eat(TokenType.ASSIGN);
        procExpr();
    }

    // <print> ::= (print | println) '(' <expr> ')'
    private void procPrint() {
        if (current.type == TokenType.PRINT) {
            advance();
        } else {
            eat(TokenType.PRINTLN);
        }

        eat(TokenType.OPEN_PAR);
        procExpr();
        eat(TokenType.CLOSE_PAR);
    }

    // <if> ::= if '(' <expr> ')' <body> [ else <body> ]
    private void procIf() {
        eat(TokenType.IF);
        eat(TokenType.OPEN_PAR);
        procExpr();
        eat(TokenType.CLOSE_PAR);
        procBody();

        if (current.type == TokenType.ELSE) {
            advance();
            procBody();
        }
    }

    // <while> ::= while '(' <expr> ')' <body>
    private void procWhile() {
        eat(TokenType.WHILE);
        eat(TokenType.OPEN_PAR);
        procExpr();
        eat(TokenType.CLOSE_PAR);
        procBody();
    }

    // <for> ::= for '(' [ ( <def> | <assign> ) { ',' ( <def> | <assign> ) } ] ';'
    // [ <expr> ] ';' [ <assign> { ',' <assign> } ] ')' <body>
    private void procFor() {

    }

    // <foreach> ::= foreach '(' [ def ] <name> in <expr> ')' <body>
    private void procForeach() {

    }

    // <body> ::= <cmd> | '{' <code> '}'
    private void procBody() {

    }

    // <assign> ::= [ <expr> ( '=' | '+=' | '-=' | '*=' | '/=' | '%=' | '**=') ]
    // <expr>
    private void procAssign() {

    }

    // <expr> ::= <rel> { ('&&' | '||') <rel> }
    private void procExpr() {
        procRel();
        while (current.type == TokenType.AND ||
                current.type == TokenType.OR) {
            if (current.type == TokenType.AND) {
                advance();
            } else {
                advance();
            }

            procRel();
        }
    }

    // <rel> ::= <cast> [ ('<' | '>' | '<=' | '>=' | '==' | '!=' | in | '!in')
    // <cast> ]
    private void procRel() {

    }

    // <cast> ::= <arith> [ as ( Boolean | Integer | String) ]
    private void procCast() {

    }

    // <arith> ::= <term> { ('+' | '-') <term> }
    private void procArith() {

    }

    // <term> ::= <power> { ('*' | '/' | '%') <power> }
    private void procTerm() {

    }

    // <power> ::= <factor> { '**' <factor> }
    private void procPower() {

    }

    // <factor> ::= [ '!' | '-' ] ( '(' <expr> ')' | <rvalue> )
    private void procFactor() {
        if (current.type == TokenType.NOT) {
            advance();
        } else if (current.type == TokenType.SUB) {
            advance();
        }

        if (current.type == TokenType.OPEN_PAR) {
            advance();
            procExpr();
            eat(TokenType.CLOSE_PAR);
        } else {
            procRvalue();
        }
    }

    // <lvalue> ::= <name> { '.' <name> | '[' <expr> ']' }
    private void procLvalue() {
    }

    // <rvalue> ::= <const> | <function> | <switch> | <struct> | <lvalue>
    private void procRvalue() {
        switch (current.type) {
            case NULL:
            case FALSE:
            case TRUE:
            case NUMBER:
            case TEXT:
                procConst();
                break;
            case READ:
            case EMPTY:
            case SIZE:
            case KEYS:
            case VALUES:
                procFunction();
                break;
            case SWITCH:
                procSwitch();
                break;
            case OPEN_BRA:
                procStruct();
                break;
            default:
                procLvalue();
        }
    }

    // <const> ::= null | false | true | <number> | <text>
    private void procConst() {
        if (current.type == TokenType.NULL) {
            advance();
        } else if (current.type == TokenType.FALSE) {
            advance();
        } else if (current.type == TokenType.TRUE) {
            advance();
        } else if (current.type == TokenType.NUMBER) {
            procNumber();
        } else if (current.type == TokenType.TEXT) {
            procText();
        } else {
            showError();
        }
    }

    // <function> ::= (read | empty | size | keys | values) '(' <expr> ')'
    private void procFunction() {

    }

    // <switch> ::= switch '(' <expr> ')' '{' { case <expr> '->' <expr> } [ default
    // '->' <expr> ] '}'
    private void procSwitch() {
        eat(TokenType.SWITCH);
        eat(TokenType.OPEN_PAR);
        procExpr();
        eat(TokenType.CLOSE_PAR);
        eat(TokenType.OPEN_CUR);
        while (current.type == TokenType.CASE) {
            advance();
            procExpr();
            eat(TokenType.ARROW);
            procExpr();
        }

        if (current.type == TokenType.DEFAULT) {
            advance();
            eat(TokenType.ARROW);
            procExpr();
        }

        eat(TokenType.CLOSE_CUR);
    }

    // <struct> ::= '[' [ ':' | <expr> { ',' <expr> } | <name> ':' <expr> { ','
    // <name> ':' <expr> } ] ']'
    private void procStruct() {

    }

    private void procName() {
        eat(TokenType.NAME);
    }

    private void procNumber() {
        eat(TokenType.NUMBER);
    }

    private void procText() {
        eat(TokenType.TEXT);
    }

}

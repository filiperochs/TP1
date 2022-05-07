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
        procCode();
        eat(TokenType.END_OF_FILE);
        return null;
    }

    private void rollback() {
        assert !history.isEmpty();

        System.out.println("Rollback (\"" + current.token + "\", " +
                current.type + ")");
        queued.push(current);
        current = history.pop();
    }

    private void advance() {
        System.out.println("Advanced (\"" + current.token + "\", " +
                current.type + ")");
        history.add(current);
        current = queued.isEmpty() ? lex.nextToken() : queued.pop();
    }

    private void eat(TokenType type) {
        System.out.println("Expected (..., " + type + "), found (\"" +
                current.token + "\", " + current.type + ")");
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
        } else if (current.type == TokenType.PRINTLN) {
            advance();
        } else {
            showError();
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

    // <for> ::= for '(' [ ( <def> | <assign> ) { ',' ( <def> | <assign> ) } ] ';' [
    // <expr> ] ';' [ <assign> { ',' <assign> } ] ')' <body>
    private void procFor() {
        // TODO: REVISAR TODO O FOR.

        eat(TokenType.FOR);
        eat(TokenType.OPEN_PAR);

        if (current.type == TokenType.DEF || current.type == TokenType.ASSIGN) {
            procDecl();

            while (current.type == TokenType.COMMA) {
                advance();

                if (current.type == TokenType.DEF || current.type == TokenType.ASSIGN) {
                    procDecl();
                } else {
                    showError();
                }

            }

        }

        eat(TokenType.SEMI_COLON);

        // TODO: Não sei definir se é uma expr.

        if (current.type == TokenType.NUMBER || current.type == TokenType.TEXT ||
                current.type == TokenType.NAME || current.type == TokenType.OPEN_PAR ||
                current.type == TokenType.OPEN_BRA || current.type == TokenType.NOT ||
                current.type == TokenType.SUB || current.type == TokenType.FALSE ||
                current.type == TokenType.TRUE) {
            procExpr();

        }

        eat(TokenType.SEMI_COLON);

        if (current.type == TokenType.DEF || current.type == TokenType.ASSIGN) {
            procDecl();

            while (current.type == TokenType.COMMA) {
                advance();

                if (current.type == TokenType.DEF || current.type == TokenType.ASSIGN) {
                    procDecl();
                } else {
                    showError();
                }
            }

        } else {
            showError();
        }

        eat(TokenType.CLOSE_PAR);

        procBody();

    }

    // <foreach> ::= foreach '(' [ def ] <name> in <expr> ')' <body>
    private void procForeach() {
        eat(TokenType.FOREACH);
        eat(TokenType.OPEN_PAR);

        if (current.type == TokenType.DEF) {
            advance();
        }

        procName();
        eat(TokenType.CONTAINS);
        procExpr();

        eat(TokenType.CLOSE_PAR);

        procBody();
    }

    // <body> ::= <cmd> | '{' <code> '}'
    private void procBody() {
        if (current.type == TokenType.OPEN_CUR) {
            advance();

            procCode();
            eat(TokenType.CLOSE_CUR);
        } else {
            procCmd();
        }

    }

    // <assign> ::= <expr> ( '=' | '+=' | '-=' | '*=' | '/=' | '%=' | '**=') <expr>
    private void procAssign() {
        procExpr();

        if (current.type == TokenType.ASSIGN
                || current.type == TokenType.ASSIGN_ADD
                || current.type == TokenType.ASSIGN_SUB
                || current.type == TokenType.ASSIGN_MUL
                || current.type == TokenType.ASSIGN_DIV
                || current.type == TokenType.ASSIGN_MOD
                || current.type == TokenType.ASSIGN_POWER) {

            advance();

            procAssign();
        }

    }

    // <expr> ::= <rel> { ('&&' | '||') <rel> }
    private void procExpr() {
        procRel();
        while (current.type == TokenType.AND ||
                current.type == TokenType.OR) {

            advance();

            procRel();
        }
    }

    // <rel> ::= <cast> [ ('<' | '>' | '<=' | '>=' | '==' | '!=' | in | '!in')
    // <cast> ]
    private void procRel() {
        procCast();

        if (current.type == TokenType.LOWER ||
                current.type == TokenType.GREATER ||
                current.type == TokenType.LOWER_EQUAL ||
                current.type == TokenType.GREATER_EQUAL ||
                current.type == TokenType.EQUALS ||
                current.type == TokenType.NOT_EQUALS ||
                current.type == TokenType.CONTAINS ||
                current.type == TokenType.NOT_CONTAINS) {

            advance();
            procCast();

        }
    }

    // <cast> ::= <arith> [ as ( Boolean | Integer | String) ]
    private void procCast() {
        procArith();

        if (current.type == TokenType.AS) {
            advance();

            if (current.type == TokenType.BOOLEAN ||
                    current.type == TokenType.INTEGER ||
                    current.type == TokenType.STRING) {
                advance();
            } else {
                showError();
            }
        }

    }

    // <arith> ::= <term> { ('+' | '-') <term> }
    private void procArith() {
        procTerm();

        while (current.type == TokenType.ADD || current.type == TokenType.SUB) {
            advance();
            procTerm();
        }

    }

    // <term> ::= <power> { ('*' | '/' | '%') <power> }
    private void procTerm() {
        procPower();

        while (current.type == TokenType.MUL || current.type == TokenType.DIV || current.type == TokenType.MOD) {
            advance();
            procPower();
        }

    }

    // <power> ::= <factor> { '**' <factor> }
    private void procPower() {
        procFactor();

        while (current.type == TokenType.POWER) {
            advance();
            procFactor();
        }
    }

    // <factor> ::= [ '!' | '-' ] ( '(' <expr> ')' | <rvalue> )
    private void procFactor() {
        if (current.type == TokenType.NOT || current.type == TokenType.SUB) {
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
        procName();

        while (current.type == TokenType.DOT ||
                current.type == TokenType.OPEN_BRA) {
            if (current.type == TokenType.DOT) {
                advance();
                procName();
            } else {
                advance();
                procExpr();
                eat(TokenType.CLOSE_BRA);
            }
        }
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
        if (current.type == TokenType.READ ||
                current.type == TokenType.EMPTY ||
                current.type == TokenType.SIZE ||
                current.type == TokenType.KEYS ||
                current.type == TokenType.VALUES) {
            advance();
        } else {
            showError();
        }

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
        eat(TokenType.OPEN_BRA);

        if (current.type == TokenType.COLON) {
            advance();
        } else if (current.type == TokenType.CLOSE_BRA) {
            // Do nothing
        } else {
            Lexeme prev = current;
            advance();

            if (prev.type == TokenType.NAME && current.type == TokenType.COLON) {
                rollback();

                procName();
                eat(TokenType.COLON);
                procExpr();

                while (current.type == TokenType.COMMA) {
                    advance();

                    procName();
                    eat(TokenType.COMMA);
                    procExpr();
                }
            } else {
                rollback();

                procExpr();

                while (current.type == TokenType.COMMA) {
                    advance();
                    procExpr();
                }
            }
        }

        eat(TokenType.CLOSE_BRA);
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

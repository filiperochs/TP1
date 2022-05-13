package syntatic;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import interpreter.command.BlocksCommand;
import interpreter.command.Command;
import interpreter.command.DeclarationCommand;
import interpreter.command.DeclarationType1Command;
import interpreter.command.DeclarationType2Command;
import interpreter.command.PrintCommand;
import interpreter.expr.ConstExpr;
import interpreter.expr.Expr;
import interpreter.expr.Variable;
import interpreter.value.BooleanValue;
import interpreter.value.NumberValue;
import interpreter.value.TextValue;
import interpreter.value.Value;
import lexical.Lexeme;
import lexical.LexicalAnalysis;
import lexical.TokenType;

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
        Command cmd = procCode();
        eat(TokenType.END_OF_FILE);
        return cmd;
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
    private BlocksCommand procCode() {
        int line = lex.getLine();

        List<Command> cmds = new ArrayList<Command>();

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
            Command c = procCmd();
            cmds.add(c);
        }

        BlocksCommand bc = new BlocksCommand(line, cmds);
        return bc;
    }

    // <cmd> ::= <decl> | <print> | <if> | <while> | <for> | <foreach> | <assign>
    private Command procCmd() {

        Command cmd = null;

        switch (current.type) {
            case DEF:
                DeclarationCommand dc = procDecl();
                cmd = dc;
                break;
            case PRINT:
            case PRINTLN:
                PrintCommand pc = procPrint();
                cmd = pc;
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

        return cmd;
    }

    // <decl> ::= def ( <decl-type1> | <decl-type2> )
    private DeclarationCommand procDecl() {
        eat(TokenType.DEF);

        DeclarationCommand dc = null;

        if (current.type == TokenType.NAME) {
            dc = procDeclType1();
        } else {
            dc = procDeclType2();
        }

        return dc;
    }

    // <decl-type1> ::= <name> [ '=' <expr> ] { ',' <name> [ '=' <expr> ] }
    private DeclarationType1Command procDeclType1() {
        Variable lhs = procName();
        int line = lex.getLine();

        Expr rhs = null;

        if (current.type == TokenType.ASSIGN) {
            advance();
            rhs = procExpr();
        }

        while (current.type == TokenType.COMMA) {
            advance();
            lhs = procName();

            if (current.type == TokenType.ASSIGN) {
                advance();
                rhs = procExpr();
            }
        }

        DeclarationType1Command dt1c = new DeclarationType1Command(line, lhs, rhs);
        return dt1c;
    }

    // <decl-type2> ::= '(' <name> { ',' <name> } ')' '=' <expr>
    private DeclarationType2Command procDeclType2() {
        eat(TokenType.OPEN_PAR);

        List<Variable> lhs = new ArrayList<Variable>();
        int line = lex.getLine();

        Variable v = procName();
        lhs.add(v);

        while (current.type == TokenType.COMMA) {
            advance();
            v = procName();
            lhs.add(v);
        }

        eat(TokenType.CLOSE_PAR);
        eat(TokenType.ASSIGN);
        Expr rhs = procExpr();

        DeclarationType2Command dt2c = new DeclarationType2Command(line, lhs, rhs);
        return dt2c;
    }

    // <print> ::= (print | println) '(' <expr> ')'
    private PrintCommand procPrint() {
        boolean newline = false;
        if (current.type == TokenType.PRINT) {
            advance();
        } else if (current.type == TokenType.PRINTLN) {
            newline = true;
            advance();
        } else {
            showError();
        }

        int line = lex.getLine();
        eat(TokenType.OPEN_PAR);

        Expr expr = procExpr();
        eat(TokenType.CLOSE_PAR);

        PrintCommand pc = new PrintCommand(line, newline, expr);
        return pc;
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
        // TODO: REVISAR.

        eat(TokenType.FOR);
        eat(TokenType.OPEN_PAR);

        if (current.type == TokenType.DEF || current.type == TokenType.NAME) {
            if (current.type == TokenType.DEF) {
                procDecl();
            } else {
                procAssign();
            }

            while (current.type == TokenType.COMMA) {
                advance();

                if (current.type == TokenType.DEF || current.type == TokenType.NAME) {
                    if (current.type == TokenType.DEF) {
                        procDecl();
                    } else {
                        procAssign();
                    }
                } else {
                    showError();
                }

            }

        }

        eat(TokenType.SEMI_COLON);

        // verificar se é <expr>
        if (current.type == TokenType.OPEN_PAR || current.type == TokenType.NAME) {
            procExpr();
        }

        eat(TokenType.SEMI_COLON);

        if (current.type == TokenType.DEF || current.type == TokenType.NAME) {
            if (current.type == TokenType.DEF) {
                procDecl();
            } else {
                procAssign();
            }

            while (current.type == TokenType.COMMA) {
                advance();

                if (current.type == TokenType.DEF || current.type == TokenType.NAME) {
                    if (current.type == TokenType.DEF) {
                        procDecl();
                    } else {
                        procAssign();
                    }
                } else {
                    showError();
                }
            }

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
    private Expr procExpr() {
        Expr expr = procRel();
        while (current.type == TokenType.AND ||
                current.type == TokenType.OR) {

            advance();

            expr = procRel();
        }

        return expr;
    }

    // <rel> ::= <cast> [ ('<' | '>' | '<=' | '>=' | '==' | '!=' | in | '!in')
    // <cast> ]
    private Expr procRel() {
        Expr expr = procCast();

        if (current.type == TokenType.LOWER ||
                current.type == TokenType.GREATER ||
                current.type == TokenType.LOWER_EQUAL ||
                current.type == TokenType.GREATER_EQUAL ||
                current.type == TokenType.EQUALS ||
                current.type == TokenType.NOT_EQUALS ||
                current.type == TokenType.CONTAINS ||
                current.type == TokenType.NOT_CONTAINS) {

            advance();
            expr = procCast();

        }

        return expr;
    }

    // <cast> ::= <arith> [ as ( Boolean | Integer | String) ]
    private Expr procCast() {
        Expr expr = procArith();

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

        return expr;

    }

    // <arith> ::= <term> { ('+' | '-') <term> }
    private Expr procArith() {
        Expr expr = procTerm();

        while (current.type == TokenType.ADD || current.type == TokenType.SUB) {
            advance();
            expr = procTerm();
        }

        return expr;
    }

    // <term> ::= <power> { ('*' | '/' | '%') <power> }
    private Expr procTerm() {
        Expr expr = procPower();

        while (current.type == TokenType.MUL || current.type == TokenType.DIV || current.type == TokenType.MOD) {
            advance();
            expr = procPower();
        }

        return expr;
    }

    // <power> ::= <factor> { '**' <factor> }
    private Expr procPower() {
        Expr expr = procFactor();

        while (current.type == TokenType.POWER) {
            advance();
            expr = procFactor();
        }

        return expr;
    }

    // <factor> ::= [ '!' | '-' ] ( '(' <expr> ')' | <rvalue> )
    private Expr procFactor() {
        Expr expr = null;
        if (current.type == TokenType.NOT || current.type == TokenType.SUB) {
            advance();
        }

        if (current.type == TokenType.OPEN_PAR) {
            advance();
            expr = procExpr();
            eat(TokenType.CLOSE_PAR);
        } else {
            expr = procRvalue();
        }

        return expr;
    }

    // <lvalue> ::= <name> { '.' <name> | '[' <expr> ']' }
    private Variable procLvalue() {
        Variable var = procName();

        while (current.type == TokenType.DOT ||
                current.type == TokenType.OPEN_BRA) {
            if (current.type == TokenType.DOT) {
                advance();
                var = procName();
            } else {
                advance();
                procExpr();
                eat(TokenType.CLOSE_BRA);
            }
        }

        return var;
    }

    // <rvalue> ::= <const> | <function> | <switch> | <struct> | <lvalue>
    private Expr procRvalue() {
        Expr expr = null;
        switch (current.type) {
            case NULL:
            case FALSE:
            case TRUE:
            case NUMBER:
            case TEXT:
                Value<?> value = procConst();
                int line = lex.getLine();
                ConstExpr constExpr = new ConstExpr(line, value);
                expr = constExpr;
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
            case NAME:
                Variable var = procLvalue();
                expr = var;
                break;
            default:
                showError();
        }

        return expr;
    }

    // <const> ::= null | false | true | <number> | <text>
    private Value<?> procConst() {
        Value<?> value = null;
        if (current.type == TokenType.NULL) {
            advance();
        } else if (current.type == TokenType.FALSE) {
            advance();
            BooleanValue bv = new BooleanValue(false);
            value = bv;
        } else if (current.type == TokenType.TRUE) {
            advance();
            BooleanValue bv = new BooleanValue(true);
            value = bv;
        } else if (current.type == TokenType.NUMBER) {
            NumberValue nv = procNumber();
            value = nv;
        } else if (current.type == TokenType.TEXT) {
            TextValue tv = procText();
            value = tv;
        } else {
            showError();
        }

        return value;
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

    private Variable procName() {
        String tmp = current.token;
        eat(TokenType.NAME);
        int line = lex.getLine();

        return new Variable(line, tmp);
    }

    private NumberValue procNumber() {
        String tmp = current.token;
        eat(TokenType.NUMBER);

        int v;
        try {
            v = Integer.parseInt(tmp);
        } catch (Exception err) {
            v = 0;
        }

        return new NumberValue(v);
    }

    private TextValue procText() {
        String tmp = current.token;

        eat(TokenType.TEXT);

        return new TextValue(tmp);
    }

}

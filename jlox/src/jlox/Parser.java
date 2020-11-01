package jlox;

import java.util.ArrayList;
import java.util.List;

public class Parser {

    private static class ParseError extends RuntimeException {}

    private final List<Token> tokens;
    private int current = 0;

    Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    List<Stmt> parse() {
        try {
            List<Stmt> statements = new ArrayList<>();
            while (!isAtEnd()) {
                statements.add(declaration());
            }
            return statements;
        } catch (ParseError error) {
            // 处理parseError是parser的责任，不应当
            // 泄漏到其他部件中
            return null;
        }
    }

    // program -> declaration*
    // declaration -> varDeclaration
    //              | statement 
    private Stmt declaration() {
        try {
            if (match(TokenType.VAR)) return varDeclaration();
            return statement();
        } catch (ParseError error) {
            synchronize();
            return null;
        }
    }

    private Stmt varDeclaration() {
        Token name = consume(TokenType.IDENTIFIER, "Expect variable name.");

        // variable's default value is null
        Expr initializer = null;
        if (match(TokenType.EQUAL)) {
            initializer = expression();
        }

        consume(TokenType.SEMICOLON, "Expect ';' after variable declartion.");
        return new Stmt.Declare(name, initializer);
    }

    // stmt -> exprStmt
    //      | printStmt
    //      | block
    private Stmt statement() {
        if (match(TokenType.PRINT)) return printStatement();
        if (match(TokenType.LEFT_BRACE)) return new Stmt.Block(block());
        return expressionStatement();
    }

    // block -> "{" declaration* "}"
    private List<Stmt> block() {
        List<Stmt> stmts = new ArrayList<>();

        while (!check(TokenType.RIGHT_BRACE) && !isAtEnd()) {
            stmts.add(declaration());
        }

        consume(TokenType.RIGHT_BRACE, "Expect right brace");
        return stmts;
    }

    // printStmt -> "print" expression ";"
    private Stmt printStatement() {
        Expr expr = expression();
        consume(TokenType.SEMICOLON, "Expect ';' after value.");
        return new Stmt.Print(expr);
    }

    // exprStmt -> expression ";"
    private Stmt expressionStatement() {
        Expr expr = expression();
        consume(TokenType.SEMICOLON, "Expect ';' after value.");
        return new Stmt.Expression(expr);
    }
    
    // expr -> comma
    private Expr expression() {
        return comma();
    }

    // comma -> assignment ("," assignment)*
    private Expr comma() {
        Expr expr = assignment();

        while (match(TokenType.COMMA)) {
            Token operator = previous();
            Expr right = assignment();
            expr = new Expr.Binary(expr, right, operator);
        }
        return expr;
    }

    // assignment -> variable "=" assignment
    //             | equality
    private Expr assignment() {
        Expr expr = equality();
        if (match(TokenType.EQUAL)) {
            Token equals = previous();
            Expr value = assignment();

            if (expr instanceof Expr.Variable) {
                // valid assignment target
                Token name = ((Expr.Variable)expr).name;
                return new Expr.Assign(name, value);
            }
            error(equals, "Invalid assignment target.");
        }
        return expr;
    }

    // equality -> comparison (("!="|"==") comparison)*
    private Expr equality() {
        Expr expr = comparison();

        while (match(TokenType.BANG_EQUAL, TokenType.EQUAL_EQUAL)) {
            Token operator = previous();
            Expr right = comparison();
            expr = new Expr.Binary(expr, right, operator);
        }

        return expr;
    }

    private boolean match(TokenType... types) {
        for (TokenType type : types) {
            if (check(type)) {
                advance();
                return true;
            }
        }
        return false;
    }

    private boolean check(TokenType type) {
        if (isAtEnd()) return false;
        return peek().type == type;
    }

    private boolean isAtEnd() {
        return peek().type == TokenType.EOF;
    }

    private Token peek() {
        return tokens.get(current);
    }

    private Token previous() {
        return tokens.get(current - 1);
    }

    private Token advance() {
        if (!isAtEnd()) current++;
        return previous();
    }

    // comparison -> term ( ( ">" | ">=" | "<" "<=" ) term )*
    private Expr comparison() {
        Expr expr = term();
        while (match(TokenType.GREATER, TokenType.GREATER_EQUAL, TokenType.LESS, TokenType.LESS_EQUAL)) {
            Token operator = previous();
            Expr right = term();
            expr = new Expr.Binary(expr, right, operator);
        }
        return expr;
    }

    // term -> factor ( ( "-" | "+" ) factor )*
    private Expr term() {
        Expr expr = factor();
        while (match(TokenType.MINUS, TokenType.PLUS)) {
            Token operator = previous();
            Expr right = factor();
            expr = new Expr.Binary(expr, right, operator);
        }
        return expr;
    }

    // factor -> factor ( "/" | "*" ) unary | unary
    private Expr factor() {
        Expr expr = unary();
        while (match(TokenType.SLASH, TokenType.STAR)) {
            Token operator = previous();
            Expr right = unary();
            expr = new Expr.Binary(expr, right, operator);
        }
        return expr;
    }

    // unary -> ("!" | "-") unary | primary
    private Expr unary() {
        if (match(TokenType.BANG, TokenType.MINUS)) {
            Token operator = previous();
            Expr right = unary();   // 左结合
            return new Expr.Unary(right, operator);
        }
        return primary();
    }

    // primary -> NUMBER | STRING | "true" | "false" | "nil" | "(" expression ") | variable
    private Expr primary() {
        if (match(TokenType.FALSE)) return new Expr.Literal(false);
        if (match(TokenType.TRUE)) return new Expr.Literal(true);
        if (match(TokenType.NIL)) return new Expr.Literal(null);

        if (match(TokenType.NUMBER, TokenType.STRING)) return new Expr.Literal(previous().literal);

        if (match(TokenType.LEFT_PAREN)) {
            Expr expr = expression();
            consume(TokenType.RIGHT_PAREN, "Expect ')' after expression.");
            return new Expr.Grouping(expr);
        }

        if (match(TokenType.IDENTIFIER)) {
            return variable();
        }

        throw error(peek(), "Expect expression.");
    }

    // variable -> IDENTIFIER
    // must call match() to assure it is a variable before this function in primary()
    private Expr variable() {
       return new Expr.Variable(previous());
    }

    private Token consume(TokenType type, String message) {
        if (check(type)) return advance();

        throw error(peek(), message);
    }

    private ParseError error(Token token, String message) {
        Lox.error(token, message);
        return new ParseError();
    }

    private void synchronize() {
        advance();

        while (!isAtEnd()) {
            if (previous().type == TokenType.SEMICOLON) return;

            switch (peek().type) {
                case CLASS:
                case FUN:
                case VAR:
                case FOR:
                case IF:
                case WHILE:
                case PRINT:
                case RETURN:
                return;
                default:
            }
            // discard tokens until reach a probable beginning of a statement
            advance();
        }
    }

}

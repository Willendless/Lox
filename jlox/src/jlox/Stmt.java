package jlox;

import java.util.List;

abstract class Stmt {

    interface Visitor<R> {
        R visitExpressionStmt(Expression stmt);
        R visitPrintStmt(Print stmt);
        R visitDeclareStmt(Declare stmt);
        R visitBlockStmt(Block stmt);
    }

    abstract <R> R accept(Visitor<R> visitor);

    static class Expression extends Stmt {
        final Expr expression;

        Expression(Expr expression) {
            this.expression = expression;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitExpressionStmt(this);
        }

    }

    static class Print extends Stmt {
        final Expr expression;

        Print(Expr expression) {
            this.expression = expression;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitPrintStmt(this);
        }

    }

    static class Declare extends Stmt {
        final Token name;
        final Expr initializer;

        Declare(Token name, Expr initializer) {
            this.name = name;
            this.initializer = initializer;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitDeclareStmt(this);
        }

    }

    static class Block extends Stmt {
        final List<Stmt> stmts;

        Block(List<Stmt> stmts) {
            this.stmts = stmts;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitBlockStmt(this);
        }

    }

}


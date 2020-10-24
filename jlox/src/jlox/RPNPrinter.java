package jlox;

import jlox.Expr.Binary;
import jlox.Expr.Grouping;
import jlox.Expr.Literal;
import jlox.Expr.Unary;

public class RPNPrinter implements Expr.Visitor<String> {

    public String print(Expr expr) {
        return expr.accept(this);
    }

    @Override
    public String visitBinaryExpr(Binary expr) {
        return parenthesize(expr.operator.lexeme, expr.left, expr.right);
    }

    @Override
    public String visitGroupingExpr(Grouping expr) {
        return parenthesize("group", expr.expression);
    }

    @Override
    public String visitUnaryExpr(Unary expr) {
        return parenthesize(expr.operator.lexeme, expr.expression);
    }

    @Override
    public String visitLiteralExpr(Literal expr) {
        if (expr.value == null)
            return "nil";
        return expr.value.toString();
    }

    private String parenthesize(String name, Expr... exprs) {
        StringBuilder builder = new StringBuilder();

        builder.append("(");
        for (Expr e : exprs) {
            builder.append(e.accept(this));
            builder.append(" ");
        }
        builder.append(name + ")");

        return builder.toString();
    }

    public static void main(String[] args) {
        Expr expression = new Expr.Binary(
                new Expr.Unary(new Expr.Literal(123), new Token(TokenType.MINUS, "-", null, 1)),
                new Expr.Grouping(new Expr.Literal(45.67)), new Token(TokenType.STAR, "*", null, 1));
        System.out.println(new RPNPrinter().print(expression));
    }

}

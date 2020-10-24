package tool;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

public class GenerateAST {
    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.err.println("Usage: generate_ast <output directory>");
            System.exit(64);
        }
        String outputdir = args[0];
        defineAST(outputdir, "Expr", Arrays.asList(
            "Binary: Expr left, Expr right, Token operator",
            "Grouping: Expr expression",
            "Unary: Expr expression, Token operator",
            "Literal: Object value"
        ));
    }

    // example；
    // abstract class Expr {
    //     static class Binary {
    //         Expr left;
    //         Expr right;
    //         Token operator;
    //     }
    //      static class Grouping {
    //     }
    //     ...
    // }
    private static void defineAST(String outputDir, String baseName, List<String> types) throws IOException {
        String path = outputDir + "/" + baseName + ".java";
        PrintWriter writer = new PrintWriter(path, "UTF-8");

        writer.println("package jlox;");
        writer.println();
        writer.println("import java.util.List;");
        writer.println("abstract class " + baseName + " {");
        writer.println();

        defineVisitor(writer, baseName, types);

        // abstract accept() method
        writer.println("    abstract <R> R accept(Visitor<R> visitor);");
        writer.println();

        // subclasses
        for (String type : types) {
            String className = type.split(":")[0].trim();
            String fields = type.split(":")[1].trim();
            defineSubClass(writer, baseName, className, fields);
        }

        writer.println("}");
        writer.println();
        writer.close();
    }

    private static void defineSubClass(PrintWriter writer, String baseName, String className, String fields) {
        // class head
        writer.println("    static class " + className + " extends " + baseName + " {");
        // fields
        String[] fieldList = fields.split(",");
        for (String field : fieldList) {
            writer.println("        final " + field.trim() + ";");
        }
        writer.println();
        // constructor
        writer.println("        " + className + "(" + fields + ")" + " {");
        for (String field : fieldList) {
            String name = field.trim().split(" ")[1];
            writer.println("            this." + name + " = " + name + ";");
        }
        writer.println("        }");
        writer.println();
        // accept() implementations
        writer.println("        @Override");
        writer.println("        <R> R accept(Visitor<R> visitor) {");
        writer.println("            return visitor.visit" + className + baseName + "(this);");
        writer.println("        }");
        writer.println();
        // close brace
        writer.println("    }");
        writer.println();
    }

    //
    // example:
    // abstract Expr {      |---------------------------------------------
    //    abstract void accept(Visitor v);<------------------            |
    // }                                                     \          |
    //                                                        \        |
    // class xxxVisitor implements Visitory <String> {         \      |
    //     String print(Expr expr) { // trigger the visit } ----     |
    //      @Override                                               |
    //      public String visitBinaryExpr() {...} <-----------------
    //      @Override
    //      public String visitGroupingExpr() {...}
    // }
    //
    // interface Visitor <T> {
    //      T visitBinaryExpr(Binary expr);
    //      T visitGroupingExpr(Gourping expr);
    //      T visitLiteralExpr(Literal expr);
    //      ...
    // }
    //
    private static void defineVisitor(PrintWriter writer, String baseName, List<String> types) {
        writer.println("    interface Visitor<R> {");
        for (String type : types) {
            String className = type.split(":")[0].trim();
            writer.println("        R visit" + className + baseName + "(" + className
                           + " " + baseName.toLowerCase() + ");");
        }
        writer.println("    }");
        writer.println();
    }

}

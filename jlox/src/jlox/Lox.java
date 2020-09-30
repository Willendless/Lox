package jlox;

public class Lox {

    static boolean hadError = false;
    public static void main(String[] args) {
        if (args.length > 1) {
            usage();
            return;
        } else if (args.length == 1) {
            runFile(args[0]);
        } else {
            runPromt();
        }
    }

    private static void runFile(String path) {


    }

    private static void runPromt() {

    }

    private static void usage() {
        System.out.println("asdf");
    }
}

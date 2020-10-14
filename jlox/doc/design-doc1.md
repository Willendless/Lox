# design doc 1: scanner

## 数据结构

### 1. Lox类

```java
public class Lox {
    // interpreter logic
    public static void main(String[] args);
    private static void runFile(String path);
    private static void runPrompt();
    private static run(String source);
    // error handling
    static void error(int line, String message);
    private static void report(int line, String where, String message);
    // global error flag
    static boolean hadError = false;
}
```

+ main: 根据参数个数，运行源文件或交互式解释器
+ runFile: 读取源文件，执行run
+ runPrompt: 循环读取每一行，执行run

### 2. TokenType枚举类

```java
enum TokenType {
    // single-character tokens.
    LEFT_PAREN, RIGHT_PAREN, LEFT_BRACE, RIGHT_BRACE,
    COMMA, DOT, MINUS, PLUS, SEMICOLON, SLASH, STAR,

    // One or two character tokens.
    BANG, BANG_EQUAL,
    EQUAL, EQUAL_EQUAL,
    GREATER, GREATER_EQUAL,
    LESS, LESS_EQUAL,

    // Literals.
    IDENTIFIER, STRING, NUMBER,

    // Keywords
    AND, CLASS, ELSE, FALSE, FUN, FOR, IF, NIL, OR,
    PRINT, RETURN, SUPER, THIS, TRUE, VAR, WHILE,

    EOF
}
```

### 3. Token类

```java
class Token {
    final TokenType type;
    final String lexeme;
    final Object literal; // runtime value
    final int line;

    Token();
    putlic Strint toString();
}
```

### 4. Scanner类

```java
class Scanner {
    private final String source;
    private final List<Token> tokens = new ArrayList();
    // scan position info
    private int start = 0;
    private int current = 0;
    private int line = 1;
    // keywords map
    private static final Map<String, String> keywords;

    Scanner();
    List<Token> scanTokens();
    private void scanToken();
}
```

+ scanTokens(): 循环扫描输入字节流
+ scanToken(): 扫描生成单个token

## 算法

1. token扫描
    + 单个字符token
    + 单/双字符token
    + 注释
    + 空白符
    + 字符串字面量
    + 整数字面量
    + 保留字和标识符

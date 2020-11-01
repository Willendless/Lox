# design-doc5: statements and state

## 语句

为语句添加grammar

```
program -> statement* EOF;

statement -> exprStmt
          |  printStmt;

exprStmt -> expression ";";
printStmt -> "print" expression ";";
```

EOF确保消费完整个token流，而非忽视未处理的错误token。

## 全局变量声明语句

+ 语句的优先级
  + 在声明语句出现的地方，其他语句例如表达式语句也可以出现。但是其他语句能出现的地方，声明语句不一定能出现。
  + 例：`if (a) var a = 5;`是不合法的
  + 因此声明语句的优先级低于其他语句

```
program -> declaration* EOF;

declaration -> varDecl
            |  statement;

varDecl -> "var" IDENTIFIER ( "=" expression )? ";";

statement -> exprStmt
          |  printStmt;

PRIMARY -> "true" | "false" | "nil"
        |  NUMBER | STRING
        | "(" expression ")"
        IDENTIFER;
```

## 环境

+ 相同变量名的变量可能映射到同一个值上，因此使用字符串作为hashmap的键
+ 从环境中获取标识符失败时
    1. 语法错误
    2. *运行时错误*
    3. 允许该行为并返回nil

```c
// 递归函数相互调用时，一定会引用还未定义的标识符
// 若在编译期查错，则可能需要多轮扫描
int a(int v) {
    if (v == 0) return true;
    return b(v - 1);
}
int b(int v) {
    if (a == 0) return false;
    return a(v - 1);
}
```

## 赋值

+ c语言中赋值操作是一个表达式
  + `a.b.c = 1`

```
expression -> assignment;
assignment -> IDENTIFIER "=" assignment
            | equality;
```
```
expression -> comma;
comma -> assignment ("," assignment)*;
assignment -> variable "=" assignment;
<!-- ternary -> equality "?" ternary ":" ternary
        |  equality -->
equality -> comparison ( ( "!=" | "==" ) comparison )*;
comparison -> term ( ( ">" | ">=" | "<" "<=" ) term )*;
term -> factor ( ( "-" | "+" ) factor )*;
factor -> factor ( "/" | "*" ) unary | unary
unary -> ( "!" | "-" ) unary
      | primary;
primary -> NUMBER
        | STRING
        | "true" | "false" | "nil"
        | "(" expression ")"
        | variable;
varialbe -> IDENTIFIER;
```

## scope

+ 词法域：程序文本本身展示了域的开始和结束。
+ 动态域：直到执行程序才知道名字对应的值。

变量是词法域的，方法和对象的field是动态域的。

```java

class a {
  play() {
    1 + 1;
  }
}

class b {
  play() {
    2 + 1;
  }
}

fun playIt(thing) {
  thing.play();
}
```

### 实现

+ 在local scope中，同名变量shadow外部scope的变量
+ 引用变量时，从当前scope开始直到global scope开始搜索，这是*parent-pointer tree*，指针需要定义成final的

```
statement -> exprStmt
           | printStmt
           | block;
block -> "{" declaration* "}";
```

+ 解释器中执行block内部语句时，创建一个指向当前env的新的env对象，同时将解释器当前环境设置为新的env。注意：利用try和finally恢复上下文。
  + 另一种方式是每一个语句对应的解释方法都有一个env参数，这样就不用手动恢复上下文。

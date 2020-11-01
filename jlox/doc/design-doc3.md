# design-doc3: Parsing Expressions

## ambiguity

+ parse的时候需要保证通过grammar生成表达式的方法的唯一性。
+ 例如：`6 / 3 - 1`
  + 可以解释为`6 / (3 - 1)`
  + 或者`(6 / 3) - 1`

---

+ *precedence*优先级
+  *associativity*关联
  + 左关联：`5 - 3 - 2` = `(5 - 3) - 2`
  + 右关联：`a = b = c` = `a = (b = c)`

---

+ precedence from lowest to highest:
  + Equality: `== !=` 左关联
  + Comparison: `> >= < <=` 左
  + Term: `- +` 左
  + Factor: `/ *` 左
  + Unary: `! -` 右
+ 为每一个优先级级别定义一个独立的规则，优先级越小的规则优先出现：

```
expression -> comma;
comma -> ternary ("," ternary)*;
ternary -> equality "?" ternary ":" ternary
        |  equality
equality -> comparison ( ( "!=" | "==" ) comparison )*;
comparison -> term ( ( ">" | ">=" | "<" "<=" ) term )*;
term -> factor ( ( "-" | "+" ) factor )*;
factor -> factor ( "/" | "*" ) unary | unary
unary -> ( "!" | "-" ) unary
      | primary;
primary -> NUMBER
        | STRING
        | "true" | "false" | "nil"
        | "(" expression ")";
```

## recursive descent parsing

递归下降parser是对规则的直接翻译。每一个规则对应一个函数。每个函数生成对应的语法树。对于左递归，该函数会调用立刻自己本身，因此会有问题。

+ grammar notation -> code representation
+ *Terminal*: 消费一个token
+ Nonterminal: 调用该规则的函数
+ |: *if*或*switch*语句
+ /* or +: *while*或*for*循环
+ ?: *if*语句

## 语法错误

给出一个token序列，parser的工作：

1. 若有效则生成对应的语法树
2. 若无效则检测到错误告诉用户
    + parser必须检测和报告错误
    + parser不能崩溃或挂起

目标：

1. 性能
2. 报告尽可能多的错误
3. 最少化级联(cascaded)错误

error recovery: parser响应一个error同时继续尝试处理之后的error。*best effort*

### panic mode

*synchronization:* 出现error时，调整状态，使得能正常处理下一个token。通常丢弃当前语句剩余的token，直到下一个语句。

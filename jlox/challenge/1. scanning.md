# challenge

1. python和haskell都具有非正则的lexical grammar，为什么
    + lexer会scan的过程中会记录缩进的级别信息[ref](http://trevorjim.com/python-is-not-context-free/)
    + TODO: 为什么
2. 什么情况下lexer处理空白符时不是直接丢弃？
    + python需要缩进级别信息，升级(INDENT)、降级(DEDENT)、换行同级(NEWLINE)分别有对应token表示，见上一个参考
3. 支持嵌套注释
    + c和c++都不支持嵌套注释，示例如下：

```c
/*
    /*hello world*/
*/

// 最后一个*/会报错
```

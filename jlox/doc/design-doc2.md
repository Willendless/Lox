# representing the code

## formal grammar

1. atomic pieces: *alphabet*
2. defines a set of "strings" that are "in" the grammar
    + *string:* a sequence of *letters* in the alphabet
3. lexical grammar:
    + alphabet: character
    + string: token
4. syntactic grammar: 
    + alphabet: token
    + string: expression

> object: *specify which strings are valid and which aren't*

### representation of grammars

+ *rule-based:* rules are called *productions* because they produce strings in the grammar
+ strings are called *derivations*: derived from rules

---

+ production/rule:
  + includes its name - *head* and *body* - a list of symbols, which describes what its generates
  + in cfg(context-free grammar): head has only a single symbol
+ symbols:
    1. *terminal*: a letter from the grammar's alphabet => token
    2. *nonterminal*: named reference to another rule in the grammar 
+ same head can have multiple productions

### expression grammar

```
expression -> literal
            | unary
            | binary
            | grouping ;

literal    -> NUMBER | STRING | "true" | "false" | "nil" ;
grouping   -> "(" expression ")" ;
unary      -> ( "-" | "!" ) expression ;
binary     -> expression operator expression

operator   -> "==" | "!=" | "<" | ">" | "<=" | ">=" | "+" | "-" | "*" | "/" ;
```

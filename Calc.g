grammar Calc;

prog
    : stat+
    ;

stat
    : expr                              # printExpr
    | ID '=' expr                       # assign
    | ID '(' parm=(ID|NUM) ')' '=' expr # func
    ;

expr
    : expr op=(MOD|MUL|DIV) expr        # ModMulDiv
    | expr op=(ADD|SUB) expr            # AddSub
    | ID '(' expr ')'                   # call
    | NUM                               # num
    | ID                                # id
    | '(' expr ')'                      # parens
    | sign=('+'|'-') expr               # unary
    ;

MOD : '%' ;

MUL : '*' ;

DIV : '/' ;

ADD : '+' ;

SUB : '-' ;

ID  : Letter (Letter|Digit)* ;

NUM : INT
    | FLT
    ;

INT : Digit+ ;

FLT : Digit+ '.' Digit*
    | '.' Digit+
    ;

WS  : [ \t\r\n]+ -> skip ;    // toss out whitespace

fragment
Letter
    : [a-zA-Z_]
    ;

fragment
Digit
    : [0-9]
    ;

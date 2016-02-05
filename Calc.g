grammar Calc;

prog
    : stat+
    ;

stat
    : expr                              # printExpr
    | ID '=' expr                       # assign
    | ID '(' parm=(ID|INT) ')' '=' expr # func
    ;

expr
    : expr op=(MOD|MUL|DIV) expr        # ModMulDiv
    | expr op=(ADD|SUB) expr            # AddSub
    | ID '(' expr ')'                   # call
    | INT                               # int
    | ID                                # id
    | '(' expr ')'                      # parens
    | sign=('+'|'-') expr               # unary
    ;

MOD : '%' ;

MUL : '*' ;

DIV : '/' ;

ADD : '+' ;

SUB : '-' ;

ID  : Letter+ ;

INT : Digit+ ;

WS  : [ \t\r\n]+ -> skip ;    // toss out whitespace

fragment
Letter
    : [a-zA-Z]
    ;

fragment
Digit
    : [0-9]
    ;

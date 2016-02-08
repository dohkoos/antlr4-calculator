grammar Calc;

prog
    : stat+
    ;

stat
    : expr NL                              # printExpr
    | ID '=' expr NL                       # assign
    | ID '(' parm=(ID|NUM) ')' '=' expr NL # func
    | NL                                   # blank
    ;

expr
    : expr op=(MOD|MUL|DIV) expr        # ModMulDiv
    | expr op=(ADD|SUB) expr            # AddSub
    | ID '(' expr ')'                   # call
    | sign=('+'|'-') primary            # unary
    | primary                           # prim
    ;

primary
    : NUM                               # num
    | ID                                # id
    | '(' expr ')'                      # parens
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

NL  : '\r'? '\n' ;        // return newline to parser (is end-statement signal)

WS  : [ \t]+ -> skip ;    // toss out whitespace

fragment
Letter
    : [a-zA-Z_]
    ;

fragment
Digit
    : [0-9]
    ;

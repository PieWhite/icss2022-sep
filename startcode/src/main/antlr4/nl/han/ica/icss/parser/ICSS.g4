grammar ICSS;

//--- LEXER: ---

// IF support:
IF: 'if';
ELSE: 'else';
BOX_BRACKET_OPEN: '[';
BOX_BRACKET_CLOSE: ']';


//Literals
TRUE: 'TRUE';
FALSE: 'FALSE';
PIXELSIZE: [0-9]+ 'px';
PERCENTAGE: [0-9]+ '%';
SCALAR: [0-9]+;


//Color value takes precedence over id idents
COLOR: '#' [0-9a-f] [0-9a-f] [0-9a-f] [0-9a-f] [0-9a-f] [0-9a-f];

//Specific identifiers for id's and css classes
ID_IDENT: '#' [a-z0-9\-]+;
CLASS_IDENT: '.' [a-z0-9\-]+;

//General identifiers
LOWER_IDENT: [a-z] [a-z0-9\-]*;
CAPITAL_IDENT: [A-Z] [A-Za-z0-9_]*;

//All whitespace is skipped
WS: [ \t\r\n]+ -> skip;

//
OPEN_BRACE: '{';
CLOSE_BRACE: '}';
SEMICOLON: ';';
COLON: ':';
ADD: '+';
SUB: '-';
MUL: '*';
ASSIGNMENT_OPERATOR: ':=';




//--- PARSER: ---
stylesheet
    : (variableAssignment | styleRule)* EOF
    ;

styleRule
    : selector '{' statement '}'
    ;

declaration
    : propertyName ':' expression ';'
    ;

propertyName
    : LOWER_IDENT
    ;

variableAssignment
    : variableReference ':=' expression ';'
    ;

//ifClause: IF BOX_BRACKET_OPEN (variableReference | bool ) BOX_BRACKET_CLOSE OPEN_BRACE statement CLOSE_BRACE elseClause?;

ifClause
    : 'if' '[' condition ']' '{' statement '}' elseClause?
    ;

//elseClause: ELSE OPEN_BRACE statement CLOSE_BRACE;

elseClause
    : 'else' '{' statement '}'
    ;

expression
    : literal
    | expression '*' expression
    | expression ('+' | '-') expression
    ;

pixelSize: PIXELSIZE;
percentage: PERCENTAGE;
scalar: SCALAR;
color: COLOR;
bool: TRUE | FALSE;

variableReference
    : CAPITAL_IDENT
    ;

literal
    : pixelSize
    | percentage
    | scalar
    | color
    | variableReference
    | bool;

condition
    : variableReference
    | bool
    ;

selector
        :
        | ID_IDENT
        | CLASS_IDENT
        | LOWER_IDENT
        ;

statement
    : (declaration
    | ifClause
    | variableAssignment)*
    ;
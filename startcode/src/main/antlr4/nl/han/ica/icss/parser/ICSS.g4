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
stylesheet: (statement)* EOF;

statement: variableAssignment
         | ifClause
         | declaration
         | selectorRule;

selectorRule: selector OPEN_BRACE statement* CLOSE_BRACE;

selector: ID_IDENT | CLASS_IDENT | LOWER_IDENT;

variableAssignment: CAPITAL_IDENT ASSIGNMENT_OPERATOR expression SEMICOLON;

ifClause: IF expression BOX_BRACKET_OPEN statement* BOX_BRACKET_CLOSE (elseClause)?;

elseClause: ELSE BOX_BRACKET_OPEN statement* BOX_BRACKET_CLOSE;

declaration: LOWER_IDENT COLON expression SEMICOLON;

expression: expression MUL expression
          | expression (ADD | SUB) expression
          | primaryExpression;



primaryExpression: PIXELSIZE
                 | PERCENTAGE
                 | SCALAR
                 | COLOR
                 | CAPITAL_IDENT
                 | TRUE
                 | FALSE
                 | BOX_BRACKET_OPEN expression BOX_BRACKET_CLOSE;


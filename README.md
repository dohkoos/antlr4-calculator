# Calculator

Calculator is a simple and intuitive antlr4 calculator.

## Prerequisites

1. Install Java 1.6+ and ANTLR v4
2. Generate ANTLR files `java org.antlr.v4.Tool Calc.g -visitor -o output`
3. Copy Listener & Visitor implementations `cp *.java output`
4. Compile `javac output/*.java`

## Usage

You can use Calculator in two ways: command mode and interactive mode.

### Command mode

This is probably the mode you'll want to use. To do this, just write a text file as the following:

``` calc.txt
193
a = 5
b = 6
a+b*2
(1+2)*3
```

then,

```
$ cd output && java Calc calc.txt
```

### Interactive mode

Instead of writing a lot of small text files, you can use interactive mode.

```
$ cd output && java Calc
193
a = 5
b = 6
a+b*2
(1+2)*3
EOF      # type ctrl-D on Unix or Ctrl+Z on Windows
```

## Features

Calculator support only the basic arithmetic operators (addition, subtraction, multiplication, and division), expressions, parenthesized expressions, integer numbers, and variables.

```
+ --> addition
- --> subtraction
* --> multiplication
/ --> division
```

## TODO

1. Add support for floating-point numbers.
2. Add support for user-defined functions.
3. Add support for modulus operator %.

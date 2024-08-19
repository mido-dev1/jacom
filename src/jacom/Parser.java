package src.jacom;

import java.util.List;

import static src.jacom.TokenType.*;

class Parser {
  // An Exception
  private static class ParseError extends RuntimeException {
  }

  private final List<Token> tokens;
  private int current = 0;

  Parser(List<Token> tokens) {
    this.tokens = tokens;
  }

  Expr parse() {
    try {
      return expression();
    } catch (ParseError error) {
      return null;
    }
  }

  // Expression
  private Expr expression() {
    return comma();
  }

  // Comma (expr1, expr2, expr3, ... exprn => exprn)
  // Grammar: comma -> equality ("," equality)*;
  private Expr comma() {
    Expr expr = ternary();

    while (match(COMMA)) {
      Token operator = previous();
      Expr rigth = equality();
      expr = new Expr.Binary(expr, operator, rigth);
    }

    return expr;
  }

  // Ternary (condition ? expression-true : expression-false) // right associative
  // Grammar ternary -> equality ("?" expression ":" ternary)?; // precedence between ? and : is ignored (C docs)
  private Expr ternary() {
    Expr expr = equality();

    if (match(QMARK)) {
      Expr expr_true = expression();
      consume(COLON, "Expect : after expression.");
      Expr expr_false = ternary();
      expr = new Expr.Ternary(expr, expr_true, expr_false);
    }

    return expr;
  }

  // For equality (==, !=)
  private Expr equality() {
    Expr expr = comparison();

    while (match(BANG_EQUAL, EQUAL_EQUAL)) {
      Token operator = previous();
      Expr right = comparison();
      expr = new Expr.Binary(expr, operator, right);
    }

    return expr;
  }

  // For comparision (<, <=, >, >=)
  private Expr comparison() {
    Expr expr = term();

    while (match(GREATER, GREATER_EQUAL, LESS, LESS_EQUAL)) {
      Token operator = previous();
      Expr right = term();
      expr = new Expr.Binary(expr, operator, right);
    }

    return expr;
  }

  // For addition/subtraction
  private Expr term() {
    Expr expr = factor();

    while (match(MINUS, PLUS)) {
      Token operator = previous();
      Expr right = factor();
      expr = new Expr.Binary(expr, operator, right);
    }

    return expr;
  }

  // For multiplication/division
  private Expr factor() {
    Expr expr = unary();

    while (match(SLASH, STAR)) {
      Token operator = previous();
      Expr right = unary();
      expr = new Expr.Binary(expr, operator, right);
    }

    return expr;
  }

  // For unary (!, -)
  private Expr unary() {
    if (match(BANG, MINUS)) {
      Token operator = previous();
      Expr right = unary();
      return new Expr.Unary(operator, right);
    }

    return primary();
  }

  // For primary (litterals)
  private Expr primary() {
    if (match(FALSE))
      return new Expr.Literal(false);
    if (match(TRUE))
      return new Expr.Literal(true);
    if (match(NIL))
      return new Expr.Literal(null);

    if (match(NUMBER, STRING)) {
      return new Expr.Literal(previous().literal);
    }

    if (match(LEFT_PAREN)) {
      Expr expr = expression();
      consume(RIGHT_PAREN, "Expect ')' after expression.");
      return new Expr.Grouping(expr);
    }

    // Error productions
    if (match(COMMA)) {
      error(previous(), "Missing left-hand operand.");
      // return comma();
      comma();
      return null;
    }

    // if (match(QMARK)) {
    // error(previous(), "Expected a condition.");
    // return expression();
    // }

    // if (match(COLON)) {
    // error(previous(), "Expected an expression.");
    // return ternary();
    // }

    if (match(BANG_EQUAL, EQUAL_EQUAL)) {
      error(previous(), "Missing left-hand operand.");
      // return equality();
      equality();
      return null;
    }

    if (match(LESS, LESS_EQUAL, GREATER, GREATER_EQUAL)) {
      error(previous(), "Missing left-hand operand.");
      // return comparison();
      comparison();
      return null;
    }

    if (match(PLUS)) {
      error(previous(), "Missing left-hand operand.");
      // return term();
      term();
      return null;
    }

    if (match(SLASH, STAR)) {
      error(previous(), "Missing left-hand operand.");
      // return factor();
      factor();
      return null;
    }

    throw error(peek(), "Expect expression.");
  }

  private boolean match(TokenType... types) {
    for (TokenType type : types) {
      if (check(type)) {
        advance();
        return true;
      }
    }

    return false;
  }

  private Token consume(TokenType type, String message) {
    if (check(type))
      return advance();

    throw error(peek(), message);
  }

  private boolean check(TokenType type) {
    if (isAtEnd())
      return false;
    return peek().type == type;
  }

  private Token advance() {
    if (!isAtEnd())
      current++;
    return previous();
  }

  private boolean isAtEnd() {
    return peek().type == EOF;
  }

  private Token peek() {
    return tokens.get(current);
  }

  private Token previous() {
    return tokens.get(current - 1);
  }

  // Update hadError in main class
  private ParseError error(Token token, String message) {
    Lox.error(token, message);
    return new ParseError();
  }

  // TODO: !?
  private void synchronize() {
    advance();

    while (!isAtEnd()) {
      if (previous().type == SEMICOLON)
        return;

      switch (peek().type) {
        case CLASS:
        case FUN:
        case VAR:
        case FOR:
        case IF:
        case WHILE:
        case PRINT:
        case RETURN:
          return;
      }

      advance();
    }
  }
}

package src.jacom;

import static src.jacom.TokenType.COLON;
import static src.jacom.TokenType.QMARK;

class AstPrinter implements Expr.Visitor<String> {
  String print(Expr expr) {
    return expr.accept(this);
  }

  @Override
  public String visitBinaryExpr(Expr.Binary expr) {
    return parenthesize(expr.operator.lexeme,
        expr.left, expr.right);
  }

  @Override
  public String visitGroupingExpr(Expr.Grouping expr) {
    return parenthesize("group", expr.expression);
  }

  @Override
  public String visitLiteralExpr(Expr.Literal expr) {
    if (expr.value == null)
      return "nil";
    return expr.value.toString();
  }

  @Override
  public String visitUnaryExpr(Expr.Unary expr) {
    return parenthesize(expr.operator.lexeme, expr.right);
  }

  @Override
  public String visitTernaryExpr(Expr.Ternary expr) {
    return ternaryPrint(expr.condition, expr.expr_true, expr.expr_false);
  }

  private String parenthesize(String name, Expr... exprs) {
    StringBuilder builder = new StringBuilder();

    builder.append("(").append(name);
    for (Expr expr : exprs) {
      builder.append(" ");
      builder.append(expr.accept(this));
    }
    builder.append(")");

    return builder.toString();
  }

  private String ternaryPrint(Expr condition, Expr expr_true, Expr expr_false) {
    StringBuilder builder = new StringBuilder();

    builder.append("(").append("if ");
    builder.append(condition.accept(this)).append(" => ");
    builder.append(expr_true.accept(this)).append(" else ");
    builder.append(expr_false.accept(this)).append(")");

    return builder.toString();
  }

  public static void main(String[] args) {
    // -123 * (45.67)
    Expr e1 = new Expr.Binary(
        new Expr.Unary(
            new Token(TokenType.MINUS, "-", null, 1),
            new Expr.Literal(123)),
        new Token(TokenType.STAR, "*", null, 1),
        new Expr.Grouping(
            new Expr.Literal(45.67)));

    // 1 + 2 * 3 - 4
    Expr e2 = new Expr.Binary(
        new Expr.Binary(
            new Expr.Literal(1),
            new Token(TokenType.PLUS, "+", null, 1),
            new Expr.Binary(
                new Expr.Literal(2),
                new Token(TokenType.STAR, "*", null, 1),
                new Expr.Literal(3))),
        new Token(TokenType.MINUS, "-", null, 1),
        new Expr.Literal(4));

    Expr e3 = new Expr.Ternary(
        new Expr.Literal(1),
        new Expr.Literal("true1"),
        new Expr.Ternary(
            new Expr.Literal(2),
            new Expr.Literal("true2"),
            new Expr.Ternary(
                new Expr.Literal(3),
                new Expr.Literal("true3"),
                new Expr.Literal("false"))));

    System.out.println(new AstPrinter().print(e1));
    System.out.println(new AstPrinter().print(e2));
    System.out.println(new AstPrinter().print(e3));
  }
}
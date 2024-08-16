package src.jacom;

class RPN_print implements Expr.Visitor<String> {
  String print(Expr expr) {
    return expr.accept(this);
  }

  @Override
  public String visitBinaryExpr(Expr.Binary expr) {
    // return rpn_print(expr.operator.lexeme,
    // expr.left, expr.right);
    return expr.left.accept(this) + " "
        + expr.right.accept(this) + " " +
        expr.operator.lexeme;
  }

  @Override
  public String visitGroupingExpr(Expr.Grouping expr) {
    // return rpn_print("group", expr.expression);
    return "(" + expr.expression.accept(this) + ")";
  }

  @Override
  public String visitLiteralExpr(Expr.Literal expr) {
    if (expr.value == null)
      return "nil";
    return expr.value.toString();
  }

  @Override
  public String visitUnaryExpr(Expr.Unary expr) {
    // return rpn_print(expr.operator.lexeme, expr.right);
    return expr.right.accept(this) + " " + expr.operator.lexeme;
  }

  // private String rpn_print(String lexeme, Expr... exprs) {
  // StringBuilder builder = new StringBuilder();
  // for (Expr expr : exprs) {
  // builder.append(" ");
  // builder.append(expr.accept(this));
  // }
  // builder.append(" ").append(lexeme);
  // return builder.toString();
  // }

  public static void main(String[] args) {
    Expr e1 = new Expr.Binary(
        new Expr.Grouping(
            new Expr.Binary(new Expr.Literal(1), new Token(TokenType.PLUS, "+", null, 1), new Expr.Literal(2))),
        new Token(TokenType.STAR, "*", null, 1),
        new Expr.Grouping(
            new Expr.Binary(new Expr.Literal(4), new Token(TokenType.MINUS, "-", null, 1), new Expr.Literal(3))));
    Expr e2 = new Expr.Unary(new Token(TokenType.BANG, "!", null, 1), new Expr.Grouping(e1));
    System.out.println(new RPN_print().print(e1));
    System.out.println(new RPN_print().print(e2));
  }
}

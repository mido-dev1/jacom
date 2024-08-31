package src.jacom;

import java.util.List;

class LoxLambda implements LoxCallable {
  private final Expr.Lambda declaration;
  private final Environment closure;

  LoxLambda(Expr.Lambda declaration, Environment closure) {
    this.closure = closure;
    this.declaration = declaration;
  }

  @Override
  public String toString() {
    return "<fn>";
  }

  @Override
  public int arity() {
    return declaration.params.size();
  }

  @Override
  public Object call(Interpreter interpreter,
      List<Object> arguments) {
    // create new env for the function scope
    Environment environment = new Environment(closure);
    // assign each arg with the respective param and add them to the env
    for (int i = 0; i < declaration.params.size(); i++) {
      environment.define(declaration.params.get(i).lexeme,
          arguments.get(i));
    }

    try {
      interpreter.executeBlock(declaration.body, environment);
    } catch (Return returnValue) {
      return returnValue.value;
    }

    return null;
  }
}

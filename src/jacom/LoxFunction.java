package src.jacom;

import java.util.List;

class LoxFunction implements LoxCallable {
  private final Stmt.Function declaration;
  private final Environment closure; // outer environment
  private final boolean isInitializer;

  LoxFunction(Stmt.Function declaration, Environment closure,
      boolean isInitializer) {
    this.isInitializer = isInitializer;
    this.closure = closure;
    this.declaration = declaration;
  }

  // bind method to class to keep 'this' unchanged
  LoxFunction bind(LoxInstance instance) {
    Environment environment = new Environment(closure);
    environment.define("this", instance);
    return new LoxFunction(declaration, environment, isInitializer);
  }

  @Override
  public String toString() {
    return "<fn " + declaration.name.lexeme + ">";
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
      // return 'this' (object)
      if (isInitializer)
        return closure.getAt(0, "this");

      return returnValue.value;
    }

    // return 'this' (object) by default
    if (isInitializer)
      return closure.getAt(0, "this");
    return null;
  }
}

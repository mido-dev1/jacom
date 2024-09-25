package src.jacom;

import java.util.HashMap;
import java.util.Map;

class LoxInstance {
  // instance have fields
  private LoxClass klass;
  private final Map<String, Object> fields = new HashMap<>();

  LoxInstance(LoxClass klass) {
    this.klass = klass;
  }

  Object get(Token name) {
    // get field
    if (fields.containsKey(name.lexeme)) {
      return fields.get(name.lexeme);
    }

    // if get a method
    LoxFunction method = klass.findMethod(name.lexeme);
    if (method != null)
      // create a env that have instance stored in 'this'
      // and make it closure of the method
      // and return the method
      return method.bind(this);

    throw new RuntimeError(name,
        "Undefined property '" + name.lexeme + "'.");
  }

  void set(Token name, Object value) {
    fields.put(name.lexeme, value);
  }

  @Override
  public String toString() {
    return klass.name + " instance";
  }
}
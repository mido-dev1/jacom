package src.jacom;

import java.util.ArrayList;
import java.util.List;

class Environment {
  // parent
  final Environment enclosing;
  private final List<Object> values = new ArrayList<>();

  Environment() {
    enclosing = null;
  }

  Environment(Environment enclosing) {
    this.enclosing = enclosing;
  }

  void define(Object value) {
    values.add(value);
  }

  Environment ancestor(int distance) {
    Environment environment = this;
    for (int i = 0; i < distance; i++) {
      environment = environment.enclosing;
    }

    return environment;
  }

  Object getAt(int distance, int index) {
    return ancestor(distance).values.get(index);
  }

  void assignAt(int distance, int index, Object value) {
    ancestor(distance).values.set(index, value);
  }
}
package jlox;

import java.util.Map;
import java.util.HashMap;

public class Environment {
    private final Map<String, Object> values = new HashMap<>();
    final Environment enclosing;

    Environment() {
        enclosing = null;
    }

    Environment(Environment env) {
        enclosing = env;
    }

    void define(String name, Object value) {
        values.put(name, value);
    }

    Object get(Token name) {
        Environment env = this;
        while (env != null
                && !env.values.containsKey(name.lexeme)) {
            env = env.enclosing;
        }

        if (env != null) {
            return env.values.get(name.lexeme);
        }

        throw new RuntimeError(name,
                "Undefined variable '" + name.lexeme + "'.");
    }

    void assign(Token name, Object value) {
        Environment env = this;
        while (env != null
                && !env.values.containsKey(name.lexeme)) {
            env = env.enclosing;
        }

        if (env != null) {
            values.put(name.lexeme, value);
            return;
        }

        throw new RuntimeError(name,
                "Undefined variable '" + name.lexeme + "'.");
    }

}

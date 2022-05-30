package interpreter.expr;

import java.util.ArrayList;
import java.util.List;

import interpreter.value.ArrayValue;
import interpreter.value.Value;

public class ArrayExpr extends Expr {
  private List<Expr> array = new ArrayList<Expr>();

  public ArrayExpr(int line, List<Expr> array) {
    super(line);

    this.array = array;
  }

  @Override
  public Value<?> expr() {
    List<Value<?>> values = new ArrayList<Value<?>>();

    for (Expr expr : array) {
      values.add(expr.expr());
    }

    return new ArrayValue(values);
  }
}

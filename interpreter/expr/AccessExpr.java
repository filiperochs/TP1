package interpreter.expr;

import interpreter.util.Utils;
import interpreter.value.ArrayValue;
import interpreter.value.MapValue;
import interpreter.value.NumberValue;
import interpreter.value.TextValue;
import interpreter.value.Value;

public class AccessExpr extends SetExpr {
  private Expr base;
  private Expr index;

  public AccessExpr(int line, Expr base, Expr index) {
    super(line);

    this.base = base;
    this.index = index;
  }

  @Override
  public Value<?> expr() {
    Value<?> baseValue = base.expr();
    Value<?> indexValue = index.expr();

    if (baseValue instanceof ArrayValue) {
      ArrayValue array = (ArrayValue) baseValue;
      int i = ((NumberValue) indexValue).value();

      if (i < 0 || i >= array.value().size()) {
        return null;
      }

      return array.value().get(i);
    } else if (baseValue instanceof MapValue) {
      MapValue map = (MapValue) baseValue;
      String key = ((TextValue) indexValue).value();

      if (!map.value().containsKey(key)) {
        return null;
      }

      return map.value().get(key);
    } else {
      Utils.abort(super.getLine());
      return null;
    }
  }

  @Override
  public void setValue(Value<?> value) {
    Value<?> indexValue = index.expr();
    Value<?> baseValue = base.expr();

    if (baseValue instanceof ArrayValue) {
      ArrayValue av = (ArrayValue) baseValue;

      int i = ((NumberValue) indexValue).value();

      av.value().add(i, value);
    } else if (baseValue instanceof MapValue) {
      TextValue tv = (TextValue) indexValue;
      MapValue mv = (MapValue) baseValue;

      String key = tv.value();
      mv.value().put(key, value);
    }
  }
}

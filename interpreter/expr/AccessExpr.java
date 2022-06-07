package interpreter.expr;

import interpreter.util.Utils;
import interpreter.value.ArrayValue;
import interpreter.value.MapValue;
import interpreter.value.NumberValue;
import interpreter.value.TextValue;
import interpreter.value.Value;

public class AccessExpr extends SetExpr {
  private SetExpr base;
  private Expr index;

  public AccessExpr(int line, SetExpr base, Expr index) {
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

      if (indexValue instanceof NumberValue) {
        int i = ((NumberValue) indexValue).value();

        if (i < 0 || i >= array.value().size()) {
          return null;
        }

        return array.value().get(i);
      } else {
        Utils.abort(this.getLine());
        return null;
      }
    } else if (baseValue instanceof MapValue) {
      MapValue map = (MapValue) baseValue;

      if (indexValue instanceof TextValue) {
        String key = ((TextValue) indexValue).value();

        if (!map.value().containsKey(key)) {
          return null;
        }

        return map.value().get(key);
      } else {
        Utils.abort(this.getLine());
        return null;
      }
    } else {
      Utils.abort(this.getLine());
      return null;
    }
  }

  @Override
  public void setValue(Value<?> value) {
    Value<?> indexValue = index.expr();
    Value<?> baseValue = base.expr();

    if (baseValue instanceof ArrayValue) {
      ArrayValue av = (ArrayValue) baseValue;

      if (indexValue instanceof NumberValue) {
        int i = ((NumberValue) indexValue).value();

        if (i < 0 || i >= av.value().size()) {
          Utils.abort(this.getLine());
          return;
        }

        av.value().set(i, value);
      } else {
        Utils.abort(this.getLine());
        return;
      }
    } else if (baseValue instanceof MapValue) {
      MapValue mv = (MapValue) baseValue;

      if (indexValue instanceof TextValue) {
        String key = ((TextValue) indexValue).value();

        if (!mv.value().containsKey(key)) {
          Utils.abort(this.getLine());
          return;
        }

        mv.value().put(key, value);
      } else {
        Utils.abort(this.getLine());
        return;
      }
    } else {
      Utils.abort(this.getLine());
      return;
    }
  }
}

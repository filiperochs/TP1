package interpreter.expr;

import interpreter.util.Utils;
import interpreter.value.BooleanValue;
import interpreter.value.NumberValue;
import interpreter.value.TextValue;
import interpreter.value.Value;

public class CastExpr extends Expr {

  public enum Op {
    BooleanOp,
    IntegerOp,
    StringOp,
  }

  private Expr expr;
  private Op op;

  public CastExpr(int line, Expr expr, Op op) {
    super(line);

    this.expr = expr;
    this.op = op;
  }

  @Override
  public Value<?> expr() {
    switch (op) {
      case BooleanOp:
        return booleanOp(expr);
      case IntegerOp:
        return integerOp(expr);
      case StringOp:
        return stringOp(expr);
      default:
        Utils.abort(super.getLine());
    }

    return null;
  }

  private Value<?> stringOp(Expr expr2) {
    Value<?> v = expr2.expr();
    return new TextValue(v.toString());
  }

  private Value<?> integerOp(Expr expr2) {
    Value<?> v = expr2.expr();

    if (v instanceof NumberValue) {
      return new NumberValue(((NumberValue) v).value());
    } else if (v instanceof TextValue) {
      return new NumberValue(Integer.parseInt(((TextValue) v).value()));
    } else if (v instanceof BooleanValue) {
      return new NumberValue(((BooleanValue) v).value() ? 1 : 0);
    } else {
      Utils.abort(super.getLine());
      return null;
    }

  }

  private Value<?> booleanOp(Expr expr2) {
    Value<?> v = expr2.expr();

    if (v instanceof NumberValue) {
      return new BooleanValue(((NumberValue) v).value() != 0);
    } else if (v instanceof TextValue) {
      return new BooleanValue(!((TextValue) v).value().isEmpty());
    } else if (v instanceof BooleanValue) {
      return v;
    } else if (v == null) {
      return new BooleanValue(false);
    } else {
      Utils.abort(super.getLine());
      return null;
    }
  }
}

package interpreter.expr;

import interpreter.util.Utils;
import interpreter.value.BooleanValue;
import interpreter.value.NumberValue;
import interpreter.value.TextValue;
import interpreter.value.Value;

public class BinaryExpr extends Expr {

  public enum Op {
    AndOp,
    OrOp,
    EqualOp,
    NotEqualOp,
    LowerThanOp,
    LowerEqualOp,
    GreaterThanOp,
    GreaterEqualOp,
    ContainsOp,
    NotContainsOp,
    AddOp,
    SubOp,
    MulOp,
    DivOp,
    ModOp,
    PowerOp;
  }

  private Expr left;
  private Expr right;
  private Op op;

  public BinaryExpr(int line, Expr left, Op op, Expr right) {
    super(line);

    this.left = left;
    this.op = op;
    this.right = right;
  }

  @Override
  public Value<?> expr() {
    Value<?> v = null;

    switch (op) {
      case AndOp:
        v = andOp(left, right);
        break;
      case OrOp:
        v = orOp(left, right);
        break;
      case EqualOp:
        v = equalOp(left, right);
        break;
      case NotEqualOp:
        v = notEqualOp(left, right);
        break;
      case LowerThanOp:
        v = lowerThanOp(left, right);
        break;
      case LowerEqualOp:
        v = lowerEqualOp(left, right);
        break;
      case GreaterThanOp:
        v = greaterThanOp(left, right);
        break;
      case GreaterEqualOp:
        v = greaterEqualOp(left, right);
        break;
      case ContainsOp:
        v = containsOp(left, right);
        break;
      case NotContainsOp:
        v = notContainsOp(left, right);
        break;
      case AddOp:
        v = addOp(left, right);
        break;
      case SubOp:
        v = subOp(left, right);
        break;
      case MulOp:
        v = mulOp(left, right);
        break;
      case DivOp:
        v = divOp(left, right);
        break;
      case ModOp:
        v = modOp(left, right);
        break;
      case PowerOp:
        v = powerOp(left, right);
        break;

      default:
        Utils.abort(super.getLine());
    }

    return v;
  }

  public Value<?> andOp(Expr left, Expr right) {
    Value<?> lvalue = left.expr();
    Value<?> rvalue = right.expr();

    if (lvalue.eval() && rvalue.eval()) {
      return new BooleanValue(true);
    } else {
      return new BooleanValue(false);
    }
  }

  public Value<?> orOp(Expr left, Expr right) {
    Value<?> lvalue = left.expr();
    Value<?> rvalue = right.expr();

    if (lvalue.eval() || rvalue.eval()) {
      return new BooleanValue(true);
    } else {
      return new BooleanValue(false);
    }
  }

  public Value<?> equalOp(Expr left, Expr right) {
    Value<?> lvalue = left.expr();
    Value<?> rvalue = right.expr();

    if (lvalue != null && rvalue != null) {
      if (lvalue.value().equals(rvalue.value())) {
        return new BooleanValue(true);
      } else {
        return new BooleanValue(false);
      }
    } else {
      return new BooleanValue(lvalue == rvalue);
    }
  }

  public Value<?> notEqualOp(Expr left, Expr right) {
    return new BooleanValue(!equalOp(left, right).eval());
  }

  public Value<?> lowerThanOp(Expr left, Expr right) {
    Value<?> lvalue = left.expr();
    Value<?> rvalue = right.expr();

    if (!(lvalue instanceof NumberValue) || !(rvalue instanceof NumberValue)) {
      Utils.abort(super.getLine());
      return null;
    }

    NumberValue lnum = (NumberValue) lvalue;
    NumberValue rnum = (NumberValue) rvalue;

    if (lnum.value() < rnum.value()) {
      return new BooleanValue(true);
    } else {
      return new BooleanValue(false);
    }
  }

  public Value<?> lowerEqualOp(Expr left, Expr right) {
    Value<?> lvalue = left.expr();
    Value<?> rvalue = right.expr();

    if (!(lvalue instanceof NumberValue) || !(rvalue instanceof NumberValue)) {
      Utils.abort(super.getLine());
      return null;
    }

    NumberValue lnum = (NumberValue) lvalue;
    NumberValue rnum = (NumberValue) rvalue;

    if (lnum.value() <= rnum.value()) {
      return new BooleanValue(true);
    } else {
      return new BooleanValue(false);
    }
  }

  public Value<?> greaterThanOp(Expr left, Expr right) {
    Value<?> lvalue = left.expr();
    Value<?> rvalue = right.expr();

    if (!(lvalue instanceof NumberValue) || !(rvalue instanceof NumberValue)) {
      Utils.abort(super.getLine());
      return null;
    }

    NumberValue lnum = (NumberValue) lvalue;
    NumberValue rnum = (NumberValue) rvalue;

    if (lnum.value() > rnum.value()) {
      return new BooleanValue(true);
    } else {
      return new BooleanValue(false);
    }
  }

  public Value<?> greaterEqualOp(Expr left, Expr right) {
    Value<?> lvalue = left.expr();
    Value<?> rvalue = right.expr();

    if (!(lvalue instanceof NumberValue) || !(rvalue instanceof NumberValue)) {
      Utils.abort(super.getLine());
      return null;
    }

    NumberValue lnum = (NumberValue) lvalue;
    NumberValue rnum = (NumberValue) rvalue;

    if (lnum.value() >= rnum.value()) {
      return new BooleanValue(true);
    } else {
      return new BooleanValue(false);
    }
  }

  public Value<?> containsOp(Expr left, Expr right) {
    Value<?> lvalue = left.expr();
    Value<?> rvalue = right.expr();

    if (!(lvalue instanceof TextValue) || !(rvalue instanceof TextValue)) {
      Utils.abort(super.getLine());
      return null;
    }

    TextValue lstr = (TextValue) lvalue;
    TextValue rstr = (TextValue) rvalue;

    if (lstr.value().contains(rstr.value())) {
      return new BooleanValue(true);
    } else {
      return new BooleanValue(false);
    }
  }

  public Value<?> notContainsOp(Expr left, Expr right) {
    return new BooleanValue(!containsOp(left, right).eval());
  }

  public Value<?> addOp(Expr left, Expr right) {
    Value<?> lvalue = left.expr();
    Value<?> rvalue = right.expr();

    if (lvalue == null || rvalue == null) {
      Utils.abort(super.getLine());
      return null;
    }

    return new TextValue(lvalue.value().toString() + rvalue.value().toString());

  }

  public Value<?> subOp(Expr left, Expr right) {
    Value<?> lvalue = left.expr();
    Value<?> rvalue = right.expr();

    if (!(lvalue instanceof NumberValue) || !(rvalue instanceof NumberValue)) {
      Utils.abort(super.getLine());
      return null;
    }

    NumberValue nvl = (NumberValue) lvalue;
    int lv = nvl.value();
    NumberValue nvr = (NumberValue) rvalue;
    int rv = nvr.value();

    NumberValue res = new NumberValue(lv - rv);

    return res;
  }

  public Value<?> mulOp(Expr left, Expr right) {
    Value<?> lvalue = left.expr();
    Value<?> rvalue = right.expr();

    if (!(lvalue instanceof NumberValue) || !(rvalue instanceof NumberValue)) {
      Utils.abort(super.getLine());
      return null;
    }

    NumberValue nvl = (NumberValue) lvalue;
    int lv = nvl.value();
    NumberValue nvr = (NumberValue) rvalue;
    int rv = nvr.value();

    NumberValue res = new NumberValue(lv * rv);

    return res;
  }

  public Value<?> divOp(Expr left, Expr right) {
    Value<?> lvalue = left.expr();
    Value<?> rvalue = right.expr();

    if (!(lvalue instanceof NumberValue) || !(rvalue instanceof NumberValue)) {
      Utils.abort(super.getLine());
      return null;
    }

    NumberValue nvl = (NumberValue) lvalue;
    int lv = nvl.value();
    NumberValue nvr = (NumberValue) rvalue;
    int rv = nvr.value();

    NumberValue res = new NumberValue(lv / rv);

    return res;
  }

  public Value<?> modOp(Expr left, Expr right) {
    Value<?> lvalue = left.expr();
    Value<?> rvalue = right.expr();

    if (!(lvalue instanceof NumberValue) || !(rvalue instanceof NumberValue)) {
      Utils.abort(super.getLine());
      return null;
    }

    NumberValue nvl = (NumberValue) lvalue;
    int lv = nvl.value();
    NumberValue nvr = (NumberValue) rvalue;
    int rv = nvr.value();

    NumberValue res = new NumberValue(lv % rv);

    return res;
  }

  public Value<?> powerOp(Expr left, Expr right) {
    Value<?> lvalue = left.expr();
    Value<?> rvalue = right.expr();

    if (!(lvalue instanceof NumberValue) || !(rvalue instanceof NumberValue)) {
      Utils.abort(super.getLine());
      return null;
    }

    NumberValue nvl = (NumberValue) lvalue;
    int lv = nvl.value();
    NumberValue nvr = (NumberValue) rvalue;
    int rv = nvr.value();

    NumberValue res = new NumberValue((int) Math.pow(lv, rv));

    return res;
  }

}

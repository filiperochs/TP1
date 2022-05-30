package interpreter.command;

import java.util.List;

import interpreter.expr.Expr;
import interpreter.expr.SetExpr;
import interpreter.util.Utils;
import interpreter.value.ArrayValue;
import interpreter.value.MapValue;
import interpreter.value.NumberValue;
import interpreter.value.TextValue;
import interpreter.value.Value;

public class AssignCommand extends Command {

  public enum Op {
    StdOp,
    AddOp,
    SubOp,
    MulOp,
    DivOp,
    ModOp,
    PowerOp;
  }

  private SetExpr lhs;
  private Op op;
  private Expr rhs;

  public AssignCommand(int line, SetExpr lhs, Op op, Expr rhs) {
    super(line);

    this.lhs = lhs;
    this.op = op;
    this.rhs = rhs;
  }

  @Override
  public void execute() {
    switch (op) {
      case StdOp:
        stdOp();
        break;
      case AddOp:
        addOp();
        break;
      case SubOp:
        subOp();
        break;
      case MulOp:
        mulOp();
        break;
      case DivOp:
        divOp();
        break;
      case ModOp:
        modOp();
        break;
      case PowerOp:
        powerOp();
        break;
      default:
        Utils.abort(super.getLine());

    }
  }

  private void stdOp() {
    Value<?> rvalue = rhs.expr();
    lhs.setValue(rvalue);
  }

  private void addOp() {
    Value<?> lvalue = lhs.expr();
    Value<?> rvalue = rhs.expr();

    if (lvalue != null) {
      if (lvalue instanceof NumberValue) {
        NumberValue lnum = (NumberValue) lvalue;
        NumberValue rnum = (NumberValue) rvalue;
        lhs.setValue(new NumberValue(lnum.value() + rnum.value()));
      } else if (lvalue instanceof TextValue) {
        TextValue lstr = (TextValue) lvalue;
        TextValue rstr = (TextValue) rvalue;
        lhs.setValue(new TextValue(lstr.value() + rstr.value()));
      } else if (lvalue instanceof ArrayValue) {
        ArrayValue larr = (ArrayValue) lvalue;
        ArrayValue rarr = (ArrayValue) rvalue;

        List<Value<?>> lvals = larr.value();
        List<Value<?>> rvals = rarr.value();
        lvals.addAll(rvals);

        lhs.setValue(new ArrayValue(lvals));
      } else if (lvalue instanceof MapValue) {
        MapValue lmap = (MapValue) lvalue;
        MapValue rmap = (MapValue) rvalue;

        lmap.value().putAll(rmap.value());

        lhs.setValue(lmap);
      } else {
        Utils.abort(super.getLine());
      }
    } else {
      lhs.setValue(rvalue);
    }

  }

  private void subOp() {
    Value<?> lvalue = lhs.expr();
    Value<?> rvalue = rhs.expr();

    if (lvalue != null) {
      if (lvalue instanceof NumberValue) {
        NumberValue lnum = (NumberValue) lvalue;
        NumberValue rnum = (NumberValue) rvalue;
        lhs.setValue(new NumberValue(lnum.value() - rnum.value()));
      } else {
        Utils.abort(super.getLine());
      }
    } else {
      lhs.setValue(rvalue);
    }
  }

  private void mulOp() {
    Value<?> lvalue = lhs.expr();
    Value<?> rvalue = rhs.expr();

    if (lvalue != null) {
      if (lvalue instanceof NumberValue) {
        NumberValue lnum = (NumberValue) lvalue;
        NumberValue rnum = (NumberValue) rvalue;
        lhs.setValue(new NumberValue(lnum.value() * rnum.value()));
      } else {
        Utils.abort(super.getLine());
      }
    } else {
      lhs.setValue(rvalue);
    }
  }

  private void divOp() {
    Value<?> lvalue = lhs.expr();
    Value<?> rvalue = rhs.expr();

    if (lvalue != null) {
      if (lvalue instanceof NumberValue) {
        NumberValue lnum = (NumberValue) lvalue;
        NumberValue rnum = (NumberValue) rvalue;
        lhs.setValue(new NumberValue(lnum.value() / rnum.value()));
      } else {
        Utils.abort(super.getLine());
      }
    } else {
      lhs.setValue(rvalue);
    }
  }

  private void modOp() {
    Value<?> lvalue = lhs.expr();
    Value<?> rvalue = rhs.expr();

    if (lvalue != null) {
      if (lvalue instanceof NumberValue) {
        NumberValue lnum = (NumberValue) lvalue;
        NumberValue rnum = (NumberValue) rvalue;
        lhs.setValue(new NumberValue(lnum.value() % rnum.value()));
      } else {
        Utils.abort(super.getLine());
      }
    } else {
      lhs.setValue(rvalue);
    }
  }

  private void powerOp() {
    Value<?> lvalue = lhs.expr();
    Value<?> rvalue = rhs.expr();

    if (lvalue != null) {
      if (lvalue instanceof NumberValue) {
        NumberValue lnum = (NumberValue) lvalue;
        NumberValue rnum = (NumberValue) rvalue;
        lhs.setValue(new NumberValue((int) Math.pow(lnum.value(), rnum.value())));
      } else {
        Utils.abort(super.getLine());
      }
    } else {
      lhs.setValue(rvalue);
    }
  }

}

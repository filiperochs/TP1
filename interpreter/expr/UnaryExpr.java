package interpreter.expr;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import interpreter.util.Utils;
import interpreter.value.ArrayValue;
import interpreter.value.BooleanValue;
import interpreter.value.MapValue;
import interpreter.value.NumberValue;
import interpreter.value.TextValue;
import interpreter.value.Value;

public class UnaryExpr extends Expr {

  public enum Op {
    NotOp,
    NegOp,
    ReadOp,
    EmptyOp,
    SizeOp,
    KeysOp,
    ValuesOp;
  }

  private Scanner scanner = new Scanner(System.in);

  private Expr expr;
  private Op op;

  public UnaryExpr(int line, Expr expr, Op op) {
    super(line);

    this.expr = expr;
    this.op = op;
  }

  @Override
  public Value<?> expr() {
    Value<?> v = null;
    switch (op) {
      case NotOp:
        v = notOp();
        break;
      case NegOp:
        v = negOp();
        break;
      case ReadOp:
        v = readOp();
        break;
      case EmptyOp:
        v = emptyOp();
        break;
      case SizeOp:
        v = sizeOp();
        break;
      case KeysOp:
        v = keysOp();
        break;
      case ValuesOp:
        v = valuesOp();
        break;
      default:
        Utils.abort(super.getLine());
    }

    return v;
  }

  private Value<?> notOp() {
    Value<?> v = expr.expr();
    boolean b = v == null ? false : v.eval();
    BooleanValue bv = new BooleanValue(!b);
    return bv;
  }

  private Value<?> negOp() {
    Value<?> v = expr.expr();
    if (!(v instanceof NumberValue))
      Utils.abort(super.getLine());

    NumberValue nv = (NumberValue) v;
    int n = nv.value();

    NumberValue res = new NumberValue(-n);
    return res;
  }

  private Value<?> readOp() {
    Value<?> v = expr.expr();
    System.out.print(v == null ? "null" : v.toString());

    String line = scanner.nextLine();
    TextValue tv = new TextValue(line);
    return tv;
  }

  private Value<?> emptyOp() {
    Value<?> v = expr.expr();

    if (v == null) {
      Utils.abort(super.getLine());
      return null;
    }

    if (v instanceof TextValue) {
      TextValue tv = (TextValue) v;
      return new BooleanValue(tv.value().isEmpty());
    } else if (v instanceof ArrayValue) {
      ArrayValue av = (ArrayValue) v;
      return new BooleanValue(av.value().size() == 0);
    } else if (v instanceof MapValue) {
      MapValue mv = (MapValue) v;
      return new BooleanValue(mv.value().size() == 0);
    } else {
      Utils.abort(super.getLine());
    }

    return null;
  }

  private Value<?> sizeOp() {
    Value<?> v = expr.expr();

    if (v == null) {
      Utils.abort(super.getLine());
      return null;
    }

    if (v instanceof ArrayValue) {
      ArrayValue av = (ArrayValue) v;
      return new NumberValue(av.value().size());
    } else if (v instanceof MapValue) {
      MapValue mv = (MapValue) v;
      return new NumberValue(mv.value().size());
    } else {
      Utils.abort(super.getLine());
    }

    return null;
  }

  private Value<?> keysOp() {
    Value<?> v = expr.expr();

    if (v == null) {
      Utils.abort(super.getLine());
      return null;
    }

    if (v instanceof MapValue) {
      MapValue mv = (MapValue) v;
      List<Value<?>> keys = new ArrayList<Value<?>>();
      for (String key : mv.value().keySet()) {
        keys.add(new TextValue(key));
      }

      return new ArrayValue(keys);
    } else {
      Utils.abort(super.getLine());
    }

    return null;
  }

  private Value<?> valuesOp() {
    Value<?> v = expr.expr();

    if (v == null) {
      Utils.abort(super.getLine());
      return null;
    }

    if (v instanceof MapValue) {
      MapValue mv = (MapValue) v;
      List<Value<?>> values = new ArrayList<Value<?>>();
      for (Value<?> value : mv.value().values()) {
        values.add(value);
      }

      return new ArrayValue(values);
    } else {
      Utils.abort(super.getLine());
    }

    return null;
  }

}
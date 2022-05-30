package interpreter.command;

import interpreter.expr.Expr;
import interpreter.expr.Variable;
import interpreter.value.Value;

public class DeclarationType1Command extends DeclarationCommand {

  private Variable lhs;

  public DeclarationType1Command(int line, Variable lhs, Expr rhs) {
    super(line, rhs);

    this.lhs = lhs;
  }

  @Override
  public void execute() {
    Value<?> value = (rhs != null ? rhs.expr() : null);
    lhs.setValue(value);
  }
}

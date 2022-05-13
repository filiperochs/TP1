package interpreter.command;

import java.util.List;

import interpreter.expr.Expr;
import interpreter.expr.Variable;
import interpreter.value.Value;

public class DeclarationType2Command extends DeclarationCommand {

  private List<Variable> lhs;

  public DeclarationType2Command(int line, List<Variable> lhs, Expr rhs) {
    super(line, rhs);

    this.lhs = lhs;
  }

  @Override
  public void execute() {
    Value<?> value = rhs.expr();
    for (Variable v : lhs) {
      v.setValue(value);
    }
  }

}

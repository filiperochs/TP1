package interpreter.command;

import java.util.List;

import interpreter.expr.Expr;
import interpreter.expr.Variable;
import interpreter.util.Utils;
import interpreter.value.ArrayValue;
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

    try {
      ArrayValue arrayValue = (ArrayValue) value;

      int i = 0;

      for (Variable variable : lhs) {
        if (!(i >= arrayValue.value().size())) {
          variable.setValue(arrayValue.value().get(i));
        } else {
          variable.setValue(null);
        }
        i++;
      }
    } catch (Exception e) {
      Utils.abort(this.getLine());
    }
  }

}

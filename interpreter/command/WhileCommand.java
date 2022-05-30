package interpreter.command;

import interpreter.expr.Expr;
import interpreter.value.Value;

public class WhileCommand extends Command {

  private Expr expr;
  private Command cmds;

  public WhileCommand(int line, Expr expr, Command cmds) {
    super(line);

    this.expr = expr;
    this.cmds = cmds;
  }

  @Override
  public void execute() {
    do {
      Value<?> value = expr.expr();
      if (value != null && value.eval())
        cmds.execute();
      else
        break;
    } while (true);
  }

}

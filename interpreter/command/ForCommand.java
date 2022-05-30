package interpreter.command;

import interpreter.expr.Expr;

public class ForCommand extends Command {
  private Command init;
  private Expr cond;
  private Command inc;
  private Command cmds;

  public ForCommand(int line, Command init, Expr cond, Command inc, Command cmds) {
    super(line);

    this.init = init;
    this.cond = cond;
    this.inc = inc;
    this.cmds = cmds;
  }

  @Override
  public void execute() {
    if (init != null) {
      init.execute();
    }

    while (cond == null || cond.expr().eval()) {
      cmds.execute();

      if (inc != null) {
        inc.execute();
      }
    }
  }
}

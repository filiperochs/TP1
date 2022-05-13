package interpreter.command;

import interpreter.expr.Expr;

public abstract class DeclarationCommand extends Command {
  protected Expr rhs;

  public DeclarationCommand(int line, Expr rhs) {
    super(line);

    this.rhs = rhs;
  }

  @Override
  public abstract void execute();
}

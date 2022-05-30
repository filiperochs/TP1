package interpreter.expr;

public class CaseItem {
  public Expr key;
  public Expr value;

  public CaseItem(Expr key, Expr value) {
    this.key = key;
    this.value = value;
  }
}

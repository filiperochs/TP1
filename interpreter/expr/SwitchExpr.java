package interpreter.expr;

import java.util.ArrayList;
import java.util.List;

import interpreter.value.Value;

public class SwitchExpr extends Expr {

  private Expr expr;
  private List<CaseItem> cases = new ArrayList<CaseItem>();
  private Expr defoult;

  public SwitchExpr(int line, Expr expr) {
    super(line);

    this.expr = expr;
  }

  public void addCase(CaseItem item) {
    cases.add(item);
  }

  public void setDefault(Expr defoult) {
    this.defoult = defoult;
  }

  @Override
  public Value<?> expr() {
    Value<?> value = expr.expr();

    for (CaseItem item : cases) {
      if (item.key.expr().equals(value)) {
        return item.value.expr();
      }
    }

    if (defoult != null) {
      return defoult.expr();
    }

    return null;
  }

}

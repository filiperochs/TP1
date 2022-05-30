package interpreter.expr;

public class MapItem {
  public String key;
  public Expr value;

  public MapItem(String key, Expr value) {
    this.key = key;
    this.value = value;
  }
}

package interpreter.expr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import interpreter.value.MapValue;
import interpreter.value.Value;

public class MapExpr extends Expr {
  private List<MapItem> array = new ArrayList<MapItem>();

  public MapExpr(int line) {
    super(line);
  }

  public void addItem(MapItem item) {
    array.add(item);
  }

  @Override
  public Value<?> expr() {
    Map<String, Value<?>> map = new HashMap<String, Value<?>>();

    for (MapItem item : array) {
      map.put(item.key, item.value.expr());
    }

    return new MapValue(map);
  }

}

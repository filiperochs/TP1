package interpreter.value;

import java.util.Map;

public class MapValue extends Value<Map<String, Value<?>>> {

    private Map<String, Value<?>> value;

    public MapValue(Map<String, Value<?>> value) {
        this.value = value;
    }

    @Override
    public Map<String, Value<?>> value() {
        return this.value;
    }

    @Override
    public boolean eval() {
        return !value.isEmpty();
    }

    @Override
    public int hashCode() {
        return this.value.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj instanceof MapValue) {
            return this.value == ((MapValue) obj).value;
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("[");

        if (this.value.isEmpty()) {
            sb.append(":");
        } else {
            for (Map.Entry<String, Value<?>> e : this.value.entrySet()) {
                String k = e.getKey();
                Value<?> v = e.getValue();

                sb.append(k);
                sb.append(":");
                sb.append(v == null ? "null" : v.toString());
                sb.append(", ");
            }

            if (sb.length() > 1)
                sb.setLength(sb.length() - 2);
        }

        sb.append("]");
        return sb.toString();
    }

}

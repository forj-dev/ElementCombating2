package forj.elementcombating.utils.attribute_creator;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Pair;

import java.util.*;

public class AttributeCreator {
    private static final Random random = new Random();
    private final List<Pair<OutputVariable, Node>> expressions = new ArrayList<>();

    public AttributeCreator(List<Pair<OutputVariable, String>> expressions) {
        for (Pair<OutputVariable, String> expression : expressions) {
            Node node = parse(expression.getRight());
            optimize(node);
            this.expressions.add(new Pair<>(expression.getLeft(), node));
        }
    }

    public void setExpressions(List<Pair<OutputVariable, String>> expressions) {
        this.expressions.clear();
        for (Pair<OutputVariable, String> expression : expressions) {
            Node node = parse(expression.getRight());
            optimize(node);
            this.expressions.add(new Pair<>(expression.getLeft(), node));
        }
    }

    public Map<String, Num> create(Map<String, Num> variables) {
        Map<String, Num> result = new HashMap<>();
        for (Pair<OutputVariable, Node> expression : expressions) {
            OutputVariable output = expression.getLeft();
            Node node = expression.getRight();
            Num value = evaluate(node, new MultiMap(result, variables));
            if (output.type == OutputVariable.Type.INT || output.type == OutputVariable.Type.BOOL)
                result.put(output.name, new Num(value.getLongValue()));
            else if (output.type == OutputVariable.Type.FLOAT)
                result.put(output.name, new Num(value.getDoubleValue()));
            else result.put(output.name, value);
        }
        return result;
    }

    public Map<String, Num> create() {
        return create(new HashMap<>());
    }

    public Map<String, Num> create(NbtCompound nbt) {
        NbtMap variables = new NbtMap(nbt);
        return create(variables);
    }

    public NbtCompound createNbt(Map<String, Num> variables) {
        NbtCompound nbt = new NbtCompound();
        Map<String, Num> result = new HashMap<>();
        for (Pair<OutputVariable, Node> expression : expressions) {
            OutputVariable output = expression.getLeft();
            Node node = expression.getRight();
            Num value = evaluate(node, new MultiMap(result, variables));
            if (output.type == OutputVariable.Type.INT || output.type == OutputVariable.Type.BOOL) {
                result.put(output.name, new Num(value.getLongValue()));
                nbt.putLong(output.name, value.getLongValue());
            }
            else if (output.type == OutputVariable.Type.FLOAT) {
                result.put(output.name, new Num(value.getDoubleValue()));
                nbt.putDouble(output.name, value.getDoubleValue());
            }
            else if (value.isDouble) {
                result.put(output.name, new Num(value.doubleValue));
                nbt.putDouble(output.name, value.doubleValue);
            }
            else {
                result.put(output.name, new Num(value.longValue));
                nbt.putLong(output.name, value.longValue);
            }
        }
        return nbt;
    }

    private static Num evaluate(Node node, Map<String, Num> variables) {
        if (node.operator == 'c') return node.value;
        if (node.operator == 'v') return variables.get(node.variable);
        if (node.operator == '@') return sqrt(evaluate(node.right, variables));
        if (node.operator == '!') return not(evaluate(node.right, variables));
        Num left = evaluate(node.left, variables);
        Num right = evaluate(node.right, variables);
        if (node.operator == '+') return add(left, right);
        if (node.operator == '-') return sub(left, right);
        if (node.operator == '*') return mul(left, right);
        if (node.operator == '/') return div(left, right);
        if (node.operator == '%') return mod(left, right);
        if (node.operator == '^') return pow(left, right);
        if (node.operator == '~') return random(left, right);
        if (node.operator == '&') return and(left, right);
        if (node.operator == '|') return or(left, right);
        if (node.operator == '=') return eq(left, right);
        if (node.operator == 'n') return neq(left, right);
        if (node.operator == 'l') return lt(left, right);
        if (node.operator == 'g') return gt(left, right);
        if (node.operator == '<') return le(left, right);
        if (node.operator == '>') return ge(left, right);
        throw new IllegalArgumentException("Unknown operator: " + node.operator);
    }

    private static Node parse(String expression) {
        Node lastValue = new Node('c', new Num(0));
        Stack<Node> stack = new Stack<>();
        for (int i = 0; i < expression.length(); i++) {
            while (i < expression.length() && Character.isWhitespace(expression.charAt(i))) i++;
            if (i == expression.length()) break;
            char c = expression.charAt(i);
            if ((c == '!' || c == '<' || c == '>') && i + 1 < expression.length() && expression.charAt(i + 1) == '=') {
                c = switch (c) {
                    case '!' -> 'n';
                    case '<' -> 'l';
                    case '>' -> 'g';
                    default -> throw new IllegalStateException("Unexpected value: " + c);
                };
                i++;
            }
            if (c == '|' || c == '&' || c == '=') {
                if (i + 1 < expression.length() && expression.charAt(i + 1) == c) i++;
                else throw new IllegalArgumentException("Invalid operator: " + c);
            }
            if (Character.isLetter(c) || c == '_') {
                int j = i + 1;
                while (j < expression.length() && (Character.isLetter(expression.charAt(j)) || Character.isDigit(expression.charAt(j)) || expression.charAt(j) == '_'))
                    j++;
                String variable = expression.substring(i, j);
                lastValue = new Node('v', variable);
                i = j - 1;
                continue;
            }
            if (Character.isDigit(c)) {
                int j = i + 1;
                while (j < expression.length() && (Character.isDigit(expression.charAt(j)) || expression.charAt(j) == '.' || expression.charAt(j) == 'e'))
                    j++;
                String number = expression.substring(i, j);
                lastValue = new Node('c', parseNumber(number));
                i = j - 1;
                continue;
            }
            if (c == '(') {
                stack.push(new Node('('));
                lastValue = new Node('c', new Num(0));
                continue;
            }
            if (c == ')') {
                stack.push(lastValue);
                while (stack.peek().operator != '(') {
                    @SuppressWarnings("UnnecessaryLocalVariable")
                    Node node = stack.pop();
                    stack.peek().right = node;
                }
                Node node = stack.pop();
                lastValue = node.right;
                continue;
            }
            if (stack.isEmpty() || getPriority(c) > getPriority(stack.peek().operator)) {
                Node node = new Node(c);
                node.left = lastValue;
                stack.push(node);
            } else {
                stack.push(lastValue);
                do {
                    Node node = stack.pop();
                    if (stack.isEmpty() || stack.peek().operator == '(') {
                        stack.push(node);
                        break;
                    }
                    stack.peek().right = node;
                } while (!stack.isEmpty() && getPriority(stack.peek().operator) > getPriority(c));
                Node node = new Node(c);
                node.left = stack.pop();
                stack.push(node);
            }
            lastValue = new Node('c', new Num(0));
        }
        stack.push(lastValue);
        while (!stack.isEmpty()) {
            Node node = stack.pop();
            if (stack.isEmpty()) {
                stack.push(node);
                break;
            }
            stack.peek().right = node;
        }
        return stack.peek();
    }

    private static int getPriority(char operator) {
        return switch (operator) {
            case '(' -> 0;
            case '&' -> 1;
            case '|' -> 2;
            case '!' -> 3;
            case '=', 'n', 'l', 'g', '<', '>' -> 4;
            case '~' -> 5;
            case '+', '-' -> 6;
            case '*', '/', '%' -> 7;
            case '^' -> 8;
            case '@' -> 9;
            default -> 999;
        };
    }

    private static Num parseNumber(String number) {
        try {
            return new Num(Long.parseLong(number));
        } catch (NumberFormatException e) {
            return new Num(Double.parseDouble(number));
        }
    }

    @SuppressWarnings("DataFlowIssue")
    private static void optimize(Node node) {
        if (node.left != null) optimize(node.left);
        if (node.right != null) optimize(node.right);
        if (node.operator == '+' && node.left.operator == 'c' && node.right.operator == 'c') {
            node.operator = 'c';
            node.value = add(node.left.value, node.right.value);
            node.left = null;
            node.right = null;
        }
        if (node.operator == '-' && node.left.operator == 'c' && node.right.operator == 'c') {
            node.operator = 'c';
            node.value = sub(node.left.value, node.right.value);
            node.left = null;
            node.right = null;
        }
        if (node.operator == '*' && node.left.operator == 'c' && node.right.operator == 'c') {
            node.operator = 'c';
            node.value = mul(node.left.value, node.right.value);
            node.left = null;
            node.right = null;
        }
        if (node.operator == '/' && node.left.operator == 'c' && node.right.operator == 'c') {
            node.operator = 'c';
            node.value = div(node.left.value, node.right.value);
            node.left = null;
            node.right = null;
        }
        if (node.operator == '%' && node.left.operator == 'c' && node.right.operator == 'c') {
            node.operator = 'c';
            node.value = mod(node.left.value, node.right.value);
            node.left = null;
            node.right = null;
        }
        if (node.operator == '^' && node.left.operator == 'c' && node.right.operator == 'c') {
            node.operator = 'c';
            node.value = pow(node.left.value, node.right.value);
            node.left = null;
            node.right = null;
        }
        if (node.operator == '@' && node.right.operator == 'c') {
            node.operator = 'c';
            node.value = sqrt(node.right.value);
            node.left = null;
            node.right = null;
        }
        if (node.operator == '!' && node.right.operator == 'c') {
            node.operator = 'c';
            node.value = not(node.right.value);
            node.left = null;
            node.right = null;
        }
        if (node.operator == '&' && node.left.operator == 'c' && node.right.operator == 'c') {
            node.operator = 'c';
            node.value = and(node.left.value, node.right.value);
            node.left = null;
            node.right = null;
        }
        if (node.operator == '|' && node.left.operator == 'c' && node.right.operator == 'c') {
            node.operator = 'c';
            node.value = or(node.left.value, node.right.value);
            node.left = null;
            node.right = null;
        }
        if (node.operator == '=' && node.left.operator == 'c' && node.right.operator == 'c') {
            node.operator = 'c';
            node.value = eq(node.left.value, node.right.value);
            node.left = null;
            node.right = null;
        }
        if (node.operator == 'n' && node.left.operator == 'c' && node.right.operator == 'c') {
            node.operator = 'c';
            node.value = neq(node.left.value, node.right.value);
            node.left = null;
            node.right = null;
        }
        if (node.operator == 'l' && node.left.operator == 'c' && node.right.operator == 'c') {
            node.operator = 'c';
            node.value = lt(node.left.value, node.right.value);
            node.left = null;
            node.right = null;
        }
        if (node.operator == 'g' && node.left.operator == 'c' && node.right.operator == 'c') {
            node.operator = 'c';
            node.value = gt(node.left.value, node.right.value);
            node.left = null;
            node.right = null;
        }
        if (node.operator == '<' && node.left.operator == 'c' && node.right.operator == 'c') {
            node.operator = 'c';
            node.value = le(node.left.value, node.right.value);
            node.left = null;
            node.right = null;
        }
        if (node.operator == '>' && node.left.operator == 'c' && node.right.operator == 'c') {
            node.operator = 'c';
            node.value = ge(node.left.value, node.right.value);
            node.left = null;
            node.right = null;
        }
    }

    public static class Num {
        boolean isDouble;
        long longValue;
        double doubleValue;

        public Num(long value) {
            this.isDouble = false;
            this.longValue = value;
        }

        public Num(double value) {
            this.isDouble = true;
            this.doubleValue = value;
        }

        public Num(boolean value) {
            this.isDouble = false;
            this.longValue = value ? 1 : 0;
        }

        Num(boolean isDouble, Number value) {
            this.isDouble = isDouble;
            if (isDouble) {
                this.doubleValue = value.doubleValue();
            } else {
                this.longValue = value.longValue();
            }
        }

        public long getLongValue() {
            if (isDouble) return (long) doubleValue;
            return longValue;
        }

        public double getDoubleValue() {
            if (isDouble) return doubleValue;
            return longValue;
        }

        public boolean getBooleanValue() {
            if (isDouble) return doubleValue != 0;
            return longValue != 0;
        }

        @Override
        public String toString() {
            if (isDouble) return Double.toString(doubleValue);
            return Long.toString(longValue);
        }
    }

    public static class OutputVariable {
        public String name;
        public Type type;

        public OutputVariable(String name, Type type) {
            this.name = name;
            this.type = type;
        }

        public enum Type {
            INT, FLOAT, BOOL, AUTO
        }
    }

    static class Node {
        /**
         * operator: + - * / % ^ ( ~(ranged random) @(sqrt) c(constant) v(variable)
         */
        char operator;
        Node left;
        Node right;
        Num value;
        String variable;

        Node(char operator) {
            this.operator = operator;
        }

        Node(char operator, String variable) {
            this.operator = operator;
            this.variable = variable;
        }

        Node(char operator, Num value) {
            this.operator = operator;
            this.value = value;
        }

        @Override
        public String toString() {
            if (operator == 'c') return value.toString();
            if (operator == 'v') return variable;
            if (left == null) return "" + operator;
            return "(" + left + operator + right + ")";
        }
    }

    private static Num add(Num a, Num b) {
        if (a.isDouble || b.isDouble) {
            return new Num(true, a.getDoubleValue() + b.getDoubleValue());
        } else {
            return new Num(false, a.getLongValue() + b.getLongValue());
        }
    }

    private static Num sub(Num a, Num b) {
        if (a.isDouble || b.isDouble) {
            return new Num(true, a.getDoubleValue() - b.getDoubleValue());
        } else {
            return new Num(false, a.getLongValue() - b.getLongValue());
        }
    }

    private static Num mul(Num a, Num b) {
        if (a.isDouble || b.isDouble) {
            return new Num(true, a.getDoubleValue() * b.getDoubleValue());
        } else {
            return new Num(false, a.getLongValue() * b.getLongValue());
        }
    }

    private static Num div(Num a, Num b) {
        if (a.isDouble || b.isDouble) {
            return new Num(true, a.getDoubleValue() / b.getDoubleValue());
        } else {
            return new Num(false, a.getLongValue() / b.getLongValue());
        }
    }

    private static Num mod(Num a, Num b) {
        if (a.isDouble || b.isDouble) {
            return new Num(true, Math.IEEEremainder(a.getDoubleValue(), b.getDoubleValue()));
        } else {
            return new Num(false, a.getLongValue() % b.getLongValue());
        }
    }

    private static Num pow(Num a, Num b) {
        if (a.isDouble || b.isDouble) {
            return new Num(true, Math.pow(a.getDoubleValue(), b.getDoubleValue()));
        } else {
            return new Num(false, (long) Math.pow(a.getLongValue(), b.getLongValue()));
        }
    }

    private static Num sqrt(Num a) {
        return new Num(true, Math.sqrt(a.getDoubleValue()));
    }

    private static Num random(Num a, Num b) {
        if (a.isDouble || b.isDouble) {
            return new Num(true, random.nextDouble(a.getDoubleValue(), b.getDoubleValue()));
        } else {
            return new Num(false, random.nextLong(a.getLongValue(), b.getLongValue() + 1));
        }
    }

    private static Num not(Num a) {
        return new Num(!a.getBooleanValue());
    }

    private static Num or(Num a, Num b) {
        return new Num(a.getBooleanValue() || b.getBooleanValue());
    }

    private static Num and(Num a, Num b) {
        return new Num(a.getBooleanValue() && b.getBooleanValue());
    }

    private static Num eq(Num a, Num b) {
        return new Num(a.getDoubleValue() == b.getDoubleValue());
    }

    private static Num neq(Num a, Num b) {
        return new Num(a.getDoubleValue() != b.getDoubleValue());
    }

    private static Num lt(Num a, Num b) {
        return new Num(a.getDoubleValue() < b.getDoubleValue());
    }

    private static Num gt(Num a, Num b) {
        return new Num(a.getDoubleValue() > b.getDoubleValue());
    }

    private static Num le(Num a, Num b) {
        return new Num(a.getDoubleValue() <= b.getDoubleValue());
    }

    private static Num ge(Num a, Num b) {
        return new Num(a.getDoubleValue() >= b.getDoubleValue());
    }
}

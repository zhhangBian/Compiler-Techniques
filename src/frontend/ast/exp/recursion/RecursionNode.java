package frontend.ast.exp.recursion;

import frontend.ast.Node;
import frontend.ast.SyntaxType;
import frontend.ast.exp.ComputeExp;
import frontend.ast.token.TokenNode;

import java.util.ArrayList;

public abstract class RecursionNode extends ComputeExp {
    protected final ArrayList<Node> nodeList;

    public RecursionNode(SyntaxType syntaxType) {
        super(syntaxType);
        this.nodeList = new ArrayList<>();
    }

    protected void AddNodeList(Node node) {
        node.Parse();
        this.nodeList.add(node);
    }

    @FunctionalInterface
    interface Generate1Node1One<T, R> {
        R apply(T t);
    }

    @FunctionalInterface
    interface Generate3Node2One<T, U, V, R> {
        R apply(T t, U u, V v);
    }

    protected void HandleRecursion(
        Generate1Node1One<Node, Node> constructor1To1,
        Generate3Node2One<Node, Node, Node, Node> constructor3To1) {
        Node exp = this.nodeList.get(0);
        if (this.nodeList.size() > 1) {
            int index = 1;
            exp = constructor1To1.apply(this.nodeList.get(0));
            while (index < this.nodeList.size() - 2) {
                // op
                Node node2 = this.nodeList.get(index++);
                // RecursionNode
                Node node3 = this.nodeList.get(index++);
                // 使用构造函数创建新的节点
                exp = constructor3To1.apply(exp, node2, node3);
            }
            this.components.add(exp);
            this.components.add(this.nodeList.get(index++));
            this.components.add(this.nodeList.get(index));
        } else {
            this.components.add(exp);
        }
    }

    public void Compute() {
        // 只有单节点
        if (this.components.size() == 1) {
            ComputeExp computeExp = (ComputeExp) this.components.get(0);
            computeExp.Compute();

            this.isConst = computeExp.GetIfConst();
            this.value = computeExp.GetValue();
        }
        // 多个节点进行计算
        else {
            ComputeExp nodeL = (ComputeExp) this.components.get(0);
            ComputeExp nodeR = (ComputeExp) this.components.get(2);
            TokenNode op = (TokenNode) this.components.get(1);

            // 进行递归计算
            nodeL.Compute();
            nodeR.Compute();

            this.isConst = nodeL.GetIfConst() & nodeR.GetIfConst();
            if (this.isConst) {
                this.value = this.Compute(nodeL.GetValue(), nodeR.GetValue(), op.GetSimpleName());
            }
        }
    }

    protected int Compute(int valueL, int valueR, String op) {
        return switch (op) {
            case "+" -> valueL + valueR;
            case "-" -> valueL - valueR;
            case "*" -> valueL * valueR;
            case "/" -> valueL / valueR;
            case "%" -> valueL % valueR;
            case ">" -> valueL > valueR ? 1 : 0;
            case "<" -> valueL < valueR ? 1 : 0;
            case ">=" -> valueL >= valueR ? 1 : 0;
            case "<=" -> valueL <= valueR ? 1 : 0;
            case "==" -> valueL == valueR ? 1 : 0;
            case "!=" -> valueL != valueR ? 1 : 0;
            case "&&" -> (valueL != 0 && valueR != 0) ? 1 : 0;
            case "||" -> (valueL != 0 || valueR != 0) ? 1 : 0;
            default -> throw new RuntimeException("invalid op");
        };
    }

    public ArrayList<Node> GetNodeList() {
        return this.nodeList;
    }
}

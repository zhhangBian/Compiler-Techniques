package frontend.ast.exp.recursion;

import frontend.ast.Node;
import frontend.ast.SyntaxType;

import java.util.ArrayList;

public abstract class RecursionNode extends Node {
    protected int value;
    protected final ArrayList<Node> nodeList;

    public RecursionNode(SyntaxType syntaxType) {
        super(syntaxType);
        this.nodeList = new ArrayList<>();
    }

    protected void AddNodeList(Node node) {
        node.Parse();
        this.nodeList.add(node);
    }

    //public abstract int ComputeValue();

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
                Node node2 = this.nodeList.get(index++);
                Node node3 = this.nodeList.get(index++);
                exp = constructor3To1.apply(exp, node2, node3); // 使用构造函数创建新的节点
            }
            this.components.add(exp);
            this.components.add(this.nodeList.get(index++));
            this.components.add(this.nodeList.get(index));
        } else {
            this.components.add(exp);
        }
    }
}

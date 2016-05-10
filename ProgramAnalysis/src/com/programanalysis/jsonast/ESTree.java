package com.programanalysis.jsonast;


import java.util.ArrayList;
import java.util.List;
import com.programanalysis.jsonast.JSONESTree.*;

/**
 * Created by lukas on 05.05.16.
 * Represents a AST in ESTree format:
 * https://github.com/estree/estree
 * Types represneted as
 */
public class ESTree {

    /**
     * The id of the tree
     * (number: [node1,node2, .. , id])
     */
    private int id;

    /**
     * Root node of type Program
     */
    private ESTreeNode root;

    private ESTreeNode[] nodes;

    private ESTree() {
    }

    private ESTree(int id, ESTreeNode root, ESTreeNode[] nodes) {
        this.id = id;
        this.root = root;
    }

    /**
     * Builds a ESTree from a JSONESTree
     * @param jsonTree the JSONESTree
     * @return an ESTree
     */
    public static ESTree buildTree(JSONESTree jsonTree) {
        JSONESTreeComp[] jsonNodes = new JSONESTreeComp[jsonTree.getNodes().size()];
        ESTreeNode[] nodes = new ESTreeNode[jsonNodes.length];
        for(JSONESTreeComp jsonNode: jsonTree.getNodes()) {
            jsonNodes[jsonNode.getId()] = jsonNode;
            nodes[jsonNode.getId()] = new ESTreeNode(jsonNode);
        }

        if(nodes.length<1)
            throw new RuntimeException("Illegal Tree");
        ESTree tree = new ESTree(jsonTree.getId(), nodes[0], nodes);
        for (ESTreeNode node : nodes) {
            for(int id : jsonNodes[node.getId()].getChildren()) {
                node.addChild(nodes[id]);
                nodes[id].setParent(node);
            }
        }
        return tree;
    }

    public int getId() {
        return id;
    }

    public ESTreeNode getRoot() {
        return root;
    }

    public ESTreeNode getNodeWithID(int id) {
        if(id<0 || id>=nodes.length)
            throw new IllegalArgumentException("Not valid id "+id);
        return nodes[id];
    }

    @Override
    public String toString() {
        return "Tree: "+ getId() + "\n" + root.toString(" |");
    }

    /**
     * Represents node in the ESTree
     */
    public static class ESTreeNode {

        /**
         * A unique id of the node
         */
        private int id;
        /**
         * The type of the node as a ESType
         * Ex. Literal, Expression etc..
         */
        private ESType type;
        /**
         * The value of the node if any
         * else null
         */
        private String value;
        /**
         * The childrens of the node
         * empty list if no childrens
         */
        private List<ESTreeNode> children;
        /**
         * The parent of the node if any
         * else null
         */
        private ESTreeNode parent = null;

        private ESTreeNode(int id, String type, String value) {
            this.id = id;
            this.type = ESType.fromStringType(type);
            this.value = value;
            this.children = new ArrayList<>();
        }

        private ESTreeNode(int id, String type, String value, List<ESTreeNode> children) {
            this.id = id;
            this.type = ESType.fromStringType(type);;
            this.value = value;
            this.children = children;
        }

        private ESTreeNode(JSONESTreeComp comp) {
            this.id = comp.getId();
            this.type = ESType.fromStringType(comp.getType());
            this.value = comp.getValue();
            this.children = new ArrayList<>();
        }

        /**
         * add a child to this node
         * @param node the child
         */
        public void addChild(ESTreeNode node) {
            children.add(node);
        }

        /**
         * Has this node a child?
         * @return true/false indicates has child
         */
        public boolean hasChild() {
            return !children.isEmpty();
        }

        /**
         *
         * @return number of children
         */
        public int numChildren() {
            return children.size();
        }

        /**
         * Get child with id [0,1,..]
         * @param id the index of the child (arraylist)
         * @return reference to the child
         */
        public ESTreeNode getChild(int id) {
            if ((id<0) || (id>=children.size()))
                throw new IllegalArgumentException("No child with index " + id);
            return children.get(id);
        }

        public int getId() {
            return id;
        }

        public ESType getType() {
            return type;
        }

        public boolean hasValue() {
            return value != null;
        }

        public String getValue() {
            return value;
        }

        public ESTreeNode getParent() {
            return parent;
        }

        public void setParent(ESTreeNode node){
            this.parent = node;
        }


        /**
         * !!Aliasing
         * @return a list of childrens
         */
        public List<ESTreeNode> getChildren() {
            return children;
        }

        /**
         * Serializes the Node as a String
         * with intent
         * @param intent the intent
         * @return this nodes representation
         */
        public String toString(String intent) {
            StringBuilder sb = new StringBuilder();
            sb.append(intent).append("(Node:").append(this.id).append(" Type:").append(type);
            if(hasValue()) {
                sb.append(" Value:").append(value);
            }
            sb.append(")");
            if(hasChild()) {
                sb.append("\n");
                for(ESTreeNode node : children)
                    sb.append(node.toString(intent +" |")).append("\n");
                sb.setLength(sb.length()-1);
                sb.append("");
            }
            return sb.toString();
        }
    }

    public enum ESType {
        Program,
        EmptyStatement,
        BlockStatement,
        ExpressionStatement,
        IfStatement,
        LabeledStatement,
        BreakStatement,
        ContinueStatement,
        SwitchStatement,
        ReturnStatement,
        ThrowStatement,
        TryStatement,
        WhileStatement,
        DoWhileStatement,
        ForStatement,
        ForInStatement,
        FunctionDeclaration,
        VariableDeclaration,
        VariableDeclarator,
        ThisExpression,
        ArrayExpression,
        ObjectExpression,
        Property,
        FunctionExpression,
        SequenceExpression,
        UnaryExpression,
        BinaryExpression,
        AssignmentExpression,
        UpdateExpression,
        LogicalExpression,
        ConditionalExpression,
        CallExpression,
        NewExpression,
        MemberExpression,
        SwitchCase,
        CatchClause,
        Identifier,
        LiteralString,
        LiteralBoolean,
        LiteralNull,
        LiteralNumber,
        LiteralRegExp,
        ArrayAccess,
        AssignmentPattern,
        UNK;

        public static ESType fromStringType(String in) {
            try {
                return ESType.valueOf(in);
            } catch (IllegalArgumentException e) {
                System.out.println("UNK type: " + in);
                return UNK;
            }
        }
    }
}

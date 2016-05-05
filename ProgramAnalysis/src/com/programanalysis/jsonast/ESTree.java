package com.programanalysis.jsonast;


import java.util.ArrayList;
import java.util.List;
import com.programanalysis.jsonast.JSONESTree.*;

/**
 * Created by lukas on 05.05.16.
 */
public class ESTree {

    private int id;

    private ESTreeNode root;

    private ESTree() {
    }

    public ESTree(int id, ESTreeNode root) {
        this.id = id;
        this.root = root;
    }

    public static ESTree buildTree(JSONESTree jsonTree) {
        JSONESTreeComp[] jsonNodes = new JSONESTreeComp[jsonTree.getNodes().size()];
        ESTreeNode[] nodes = new ESTreeNode[jsonNodes.length];
        for(JSONESTreeComp jsonNode: jsonTree.getNodes()) {
            jsonNodes[jsonNode.getId()] = jsonNode;
            nodes[jsonNode.getId()] = new ESTreeNode(jsonNode);
        }

        if(nodes.length<1)
            throw new RuntimeException("Illegal Tree");
        ESTree tree = new ESTree(jsonTree.getId(), nodes[0]);
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

    public static class ESTreeNode {
        private int id;
        private String type;
        private String value;
        private List<ESTreeNode> children;
        private ESTreeNode parent = null;

        public ESTreeNode(int id, String type, String value) {
            this.id = id;
            this.type = type;
            this.value = value;
            this.children = new ArrayList<>();
        }

        public ESTreeNode(int id, String type, String value, List<ESTreeNode> children) {
            this.id = id;
            this.type = type;
            this.value = value;
            this.children = children;
        }

        public ESTreeNode(JSONESTreeComp comp) {
            this.id = comp.getId();
            this.type = comp.getType();
            this.value = comp.getValue();
            this.children = new ArrayList<>();
        }

        public void addChild(ESTreeNode node) {
            children.add(node);
        }

        public boolean hasChild() {
            return !children.isEmpty();
        }

        public int numChildren() {
            return children.size();
        }

        public ESTreeNode getChild(int id) {
            if ((id<0) || (id>=children.size()))
                throw new IllegalArgumentException("No child with index " + id);
            return children.get(id);
        }

        public int getId() {
            return id;
        }

        public String getType() {
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
         * @return
         */
        public List<ESTreeNode> getChildren() {
            return children;
        }

        public String toString(String intent) {
            StringBuilder sb = new StringBuilder();
            sb.append(intent).append("(Node:").append(this.id).append(" Type:").append(type);
            if(hasValue()) {
                sb.append(" Value:").append(value);
            }
            if(hasChild()) {
                sb.append(" Childs:\n");
                for(ESTreeNode node : children)
                    sb.append(node.toString(intent +"-")).append("\n");
                sb.setLength(sb.length()-1);
                sb.append("");
            }
            return sb.toString();
        }
    }

    @Override
    public String toString() {
        return "Tree: "+ getId() + "\n" + root.toString("-");
    }
}

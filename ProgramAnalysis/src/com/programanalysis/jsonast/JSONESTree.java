package com.programanalysis.jsonast;


import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lukas on 05.05.16.
 */
public class JSONESTree {

    private int id;

    private ArrayList<JSONESTreeComp> nodes;

    private JSONESTree(int id, ArrayList<JSONESTreeComp> nodes) {
        this.id = id;
        this.nodes = nodes;
    }

    public static JSONESTree parseLine(String line) {
        Gson gson = new Gson();
        JsonElement parsed = new JsonParser().parse(line);
        JsonArray arr = parsed.getAsJsonArray();
        ArrayList<JSONESTreeComp> res = new ArrayList<>();
        for(int i=0; i<arr.size()-1; i++) {
            JSONESTreeComp tree = gson.fromJson(arr.get(i), JSONESTreeComp.class);
            res.add(tree);
        }
        int id = arr.get(arr.size()-1).getAsInt();
        return new JSONESTree(id, res);
    }

    public int getId() {
        return id;
    }

    public ArrayList<JSONESTreeComp> getNodes() {
        return nodes;
    }

    public static class JSONESTreeComp {

        @SerializedName("id")
        @Expose
        private Integer id;
        @SerializedName("type")
        @Expose
        private String type;
        @SerializedName("children")
        @Expose
        private List<Integer> children = new ArrayList<Integer>();
        @SerializedName("value")
        @Expose
        private String value;

        /**
         *
         * @return
         * The id
         */
        public Integer getId() {
            return id;
        }

        /**
         *
         * @param id
         * The id
         */
        public void setId(Integer id) {
            this.id = id;
        }

        /**
         *
         * @return
         * The type
         */
        public String getType() {
            return type;
        }

        /**
         *
         * @param type
         * The type
         */
        public void setType(String type) {
            this.type = type;
        }

        /**
         *
         * @return
         * The children
         */
        public List<Integer> getChildren() {
            return children;
        }

        /**
         *
         * @param children
         * The children
         */
        public void setChildren(List<Integer> children) {
            this.children = children;
        }

        /**
         *
         * @return
         * The value
         */
        public String getValue() {
            return value;
        }

        /**
         *
         * @param value
         * The value
         */
        public void setValue(String value) {
            this.value = value;
        }

    }
}

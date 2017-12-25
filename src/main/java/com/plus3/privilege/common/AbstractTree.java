package com.plus3.privilege.common;

import com.alibaba.fastjson.JSONObject;

import java.util.*;

//=======================================================================================
class Test {
    public int value;
    public int id;
    public int parentId;

    Test(int value, int id, int parentId) {
        this.value = value;
        this.id = id;
        this.parentId = parentId;
    }
}

//=======================================================================================
public class AbstractTree<T> {
    public static class TreeNode<T> {
        public List<TreeNode<T>> children;
        public T value;
        public int parentId;
        public transient boolean hasParent = false;

        TreeNode(T value, int parentId) {
            this.value = value;
            this.parentId = parentId;
        }

        void addChild(TreeNode<T> treeNode) {
            if (children == null)
                children = new ArrayList<>();
            children.add(treeNode);
            treeNode.hasParent = true;
        }
    }

    //===================================================================================
    private Map<Integer, TreeNode<T>> nodeMap = new HashMap<>();

    public void addNode(T value, int id, int parentId) {
        TreeNode<T> treeNode = new TreeNode<>(value, parentId);
        nodeMap.put(id, treeNode);
    }

    public List<TreeNode<T>> buildTree() {
        Set<Integer> keySet = nodeMap.keySet();
        for (Integer key : keySet) {
            TreeNode<T> treeNode = nodeMap.get(key);

            TreeNode<T> parentNode = nodeMap.get(treeNode.parentId);
            if (parentNode != null)
                parentNode.addChild(treeNode);
        }

        //
        List<TreeNode<T>> rootNodes = new ArrayList<>();
        for (Integer key : keySet) {
            TreeNode<T> treeNode = nodeMap.get(key);
            if (treeNode.hasParent == false)
                rootNodes.add(treeNode);
        }

        return rootNodes;
    }

    //===================================================================================
    public static void main(String[] args) {
        List<Test> testList = new ArrayList<>();
        Test obj1 = new Test(1, 1, -1); testList.add(obj1);
        Test obj2 = new Test(2, 2, -1); testList.add(obj2);
        Test obj3 = new Test(3, 3, 1); testList.add(obj3);
        Test obj4 = new Test(4, 4, 1); testList.add(obj4);
        Test obj5 = new Test(5, 5, 1); testList.add(obj5);
        Test obj6 = new Test(6, 6, 3); testList.add(obj6);
        Test obj7 = new Test(7, 7, 3); testList.add(obj7);
        Test obj8 = new Test(8, 8, 2); testList.add(obj8);
        Test obj9 = new Test(9, 9, 2); testList.add(obj9);

        AbstractTree<Test> testAbstractTree = new AbstractTree<>();
        for (Test obj : testList)
            testAbstractTree.addNode(obj, obj.id, obj.parentId);

        List<TreeNode<Test>> treeNodeList = testAbstractTree.buildTree();
        System.out.println(treeNodeList.size());

        String jsonString = JSONObject.toJSONString(treeNodeList);
        System.out.println(jsonString);
        System.out.println(jsonString);
    }
}

package com.xdja.contact.view.TreeView.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zxq on 2015/6/15.
 */
public class TreeNode<S> {

    /**
     * ID
     */
    private String id;

    /**
     * 名称
     */
    private String name;

    /**
     * 是否叶子节点
     */
    private boolean leaf;

    /**
     * 是否展开
     */
    private boolean isExpand = false;

    /**
     * 下一级的子Node
     */
    private List<TreeNode> children = new ArrayList<TreeNode>();

    /**
     * 源对象
     */
    private S source;

    /**
     * 图标地址
     */
    private String iconUrl;

    /**
     * 父Node
     */
    private TreeNode parent;

    private boolean isChecked;

    public TreeNode() {
    }

    public TreeNode(String id, String parentId, String name, boolean leaf) {
        this.id = id;
        this.name = name;
        this.leaf = leaf;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取level
     */
    public int getLevel() {
        return parent == null ? 0 : parent.getLevel() +6;
    }

    public boolean isLeaf() {
        return leaf;
    }

    public void setLeaf(boolean leaf) {
        this.leaf = leaf;
    }

    public boolean isExpand() {
        return isExpand;
    }

    public void setExpand(boolean isExpand) {
        this.isExpand = isExpand;
    }

    public List<TreeNode> getChildren() {
        return children;
    }

    public void setChildren(List<TreeNode> children) {
        for (TreeNode child : children) {
            child.setParent(this);
        }
        this.children = children;
    }

    public void addChild(TreeNode child) {
        child.setParent(this);
        this.children.add(child);
    }
    public void addChild(List<TreeNode> childs) {
        for (TreeNode child : childs) {
            child.setParent(this);
        }
        this.children.addAll(childs);
    }

    public TreeNode getParent() {
        return parent;
    }

    public void setParent(TreeNode parent) {
        this.parent = parent;
    }

    /**
     * 判断父节点是否展开
     *
     * @return
     */
    public boolean isParentExpand() {
        if (parent == null)
            return false;
        return parent.isExpand();
    }

    public S getSource() {
        return source;
    }

    public boolean hasChildren() {
        return (children != null && !children.isEmpty());
    }

    public void setSource(S source) {
        this.source = source;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setIsChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }
}

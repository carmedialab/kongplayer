package com.bely.kongplayer.shared;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MediaBrowseTreee extends ExpandableListView {

    TreeAdapter adapter;
    public MediaBrowseTreee(Context context) {
        super(context);
        init(context);
    }

    public MediaBrowseTreee(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MediaBrowseTreee(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public MediaBrowseTreee(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                // 处理子节点点击事件
                return true;
            }
        });

        setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                boolean groupExpanded = parent.isGroupExpanded(groupPosition);
                if (groupExpanded) {
                    parent.collapseGroup(groupPosition);
                } else {
                    parent.expandGroup(groupPosition, true);
                }

                return false;
            }
        });

        adapter = new TreeAdapter(context);
        setAdapter(adapter);
        List<TreeNode> nodeList = new ArrayList<>();
        nodeList.add(new TreeNode("Root", true, null));
        adapter.setList(nodeList);
    }

    public class TreeAdapter extends BaseExpandableListAdapter {
        private List<TreeNode> parentNodeList = new ArrayList<>();
        private Context context;

        public TreeAdapter(Context ctx) {
            context = ctx;
        }

        public void setList(List<TreeNode> list) {
            parentNodeList.clear();
            if (list != null) {
                parentNodeList.addAll(list);
            }
        }

        @Override
        public int getGroupCount() {
            return parentNodeList.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            List<TreeNode> childNodes = parentNodeList.get(groupPosition).getChildNodes();
            int childcont = childNodes != null ? childNodes.size() : 0;
            return childcont;
        }

        @Override
        public Object getGroup(int groupPosition) {
            return parentNodeList.get(groupPosition);
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return null == parentNodeList.get(groupPosition).getChildNodes() ? null :
                    parentNodeList.get(groupPosition).getChildNodes().get(childPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.group_layout, null);
            }

            TextView groupNameTextView = convertView.findViewById(R.id.groupNameTextView);
            groupNameTextView.setText(parentNodeList.get(groupPosition).getNodeName());

            if (parentNodeList.get(groupPosition).isBrowsable) {
                // 根据 isExpanded 设置展开/折叠图标，你可以根据需要自定义
                ImageView groupIndicatorImageView = convertView.findViewById(R.id.groupIndicatorImageView);
                groupIndicatorImageView.setImageResource(android.R.drawable.btn_plus);
                groupIndicatorImageView.setOnClickListener(v -> {
                    TreeNode newNode = new TreeNode("test", false, null);
                    parentNodeList.get(groupPosition).addChild(newNode);
                    // 直接通过适配器更新数据并刷新视图
                    adapter.setList(parentNodeList);
                    adapter.notifyDataSetChanged();
                });
            }

            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.child_layout, null);
            }

            TextView childNameTextView = convertView.findViewById(R.id.childName);
            childNameTextView.setText(parentNodeList.get(groupPosition).getChildNodes().get(childPosition).getNodeName());

            // 其他子节点视图的初始化

            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }

    public class TreeNode {
        private String nodeName;
        private boolean isBrowsable;
        private List<TreeNode> childNodes;

        public TreeNode(String name, boolean browsable, List<TreeNode> children) {
            nodeName = name;
            isBrowsable = browsable;
            childNodes = children;
        }

        public String getNodeName() {
            return nodeName;
        }

        public List<TreeNode> getChildNodes() {
            return childNodes;
        }

        public void addChild(TreeNode node) {
            if (childNodes == null) {
                childNodes = new ArrayList<>();
            }
            childNodes.add(node);
        }
    }


}

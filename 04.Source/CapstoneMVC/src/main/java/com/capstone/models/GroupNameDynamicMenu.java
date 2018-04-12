package com.capstone.models;

public class GroupNameDynamicMenu {
    private String functionGroup;
    private String groupName;

    public GroupNameDynamicMenu(String functionGroup, String groupName) {
        this.functionGroup = functionGroup;
        this.groupName = groupName;
    }

    public GroupNameDynamicMenu() {
    }

    public String getFunctionGroup() {
        return functionGroup;
    }

    public void setFunctionGroup(String functionGroup) {
        this.functionGroup = functionGroup;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    @Override
    public boolean equals(Object obj) {
        boolean result = false;
        if(obj != null && obj instanceof GroupNameDynamicMenu){

         result = this.groupName.equalsIgnoreCase(((GroupNameDynamicMenu) obj).getGroupName());
        }
        return result;
    }
}

package com.jike.controller.sys.request;

import lombok.Data;

import java.util.List;

@Data
public class UpdateRolePermissionsReqeust {
    private int roleId;
    private List<Integer> permissionIds;
}

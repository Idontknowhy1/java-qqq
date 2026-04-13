package com.jike.constant;

import lombok.Getter;

@Getter
public enum PermissionsType {

    MENU("MENU", "菜单"),
    FUNC("FUNC", "功能点");

   private final String code;
   private final String name;

   PermissionsType(String code, String name) {
       this.code = code;
       this.name = name;
   }
}

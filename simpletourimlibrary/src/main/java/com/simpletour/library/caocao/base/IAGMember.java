package com.simpletour.library.caocao.base;

import java.io.Serializable;

/**
 * 包名：com.simpletour.library.caocao.base
 * 描述：成员
 * 创建者：yankebin
 * 日期：2017/5/16
 */
public interface IAGMember extends Serializable {
    IAGUser user();

    IAGMember.RoleType roleType();

    public static enum RoleType {
        MASTER(1),
        ADMIN(2),
        ORDINARY(3),
        UNKNOWN(-1);

        private int type;

        private RoleType(int type) {
            this.type = type;
        }

        public int typeValue() {
            return this.type;
        }

        public static RoleType fromValue(int value) {
            IAGMember.RoleType[] arr = values();
            int len = arr.length;

            for (int i = 0; i < len; ++i) {
                IAGMember.RoleType t = arr[i];
                if (t.typeValue() == value) {
                    return t;
                }
            }

            return UNKNOWN;
        }
    }
}
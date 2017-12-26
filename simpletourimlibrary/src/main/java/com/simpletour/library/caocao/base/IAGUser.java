package com.simpletour.library.caocao.base;

import java.io.Serializable;

/**
 * 包名：com.simpletour.library.caocao.base
 * 描述：用户模型
 * 创建者：yankebin
 * 日期：2017/5/8
 */

public interface IAGUser extends Serializable {

    long openId();

    long version();

    String nickname();

    String nicknamePinyin();

    int gender();

    String avatar();

    String remark();

    long birthday();

    String city();

    String countryCode();

    String mobile();

    boolean isActive();

    String alias();

    String aliasPinyin();

    public static enum Gender {
        UNKNOWN(-1),
        MALE(1),
        FEMALE(2);

        private int type;

        private Gender(int type) {
            this.type = type;
        }

        public int typeValue() {
            return this.type;
        }

        public static IAGUser.Gender fromValue(int value) {
            IAGUser.Gender[] arr = values();
            int len = arr.length;

            for(int i = 0; i < len; ++i) {
                IAGUser.Gender t = arr[i];
                if(t.typeValue() == value) {
                    return t;
                }
            }

            return UNKNOWN;
        }
    }
}

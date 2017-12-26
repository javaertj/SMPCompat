package com.simpletour.smtcompat;

import com.drivingassisstantHouse.library.tools.ToolBytes;

/**
 * 包名：com.simpletour.smtcompat
 * 描述：测试类
 * 创建者：yankebin
 * 日期：2017/12/26
 */
class Test {
    private void cc() {
        System.out.print(ToolBytes.bytesToHexString("123456".getBytes()));
    }

    public static void main(String[] args) {
        new Test().cc();
    }
}

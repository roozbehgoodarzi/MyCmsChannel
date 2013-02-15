package com.bps.sw;

import org.jpos.iso.ISOException;
import org.jpos.iso.ISOUtil;

public class Test {
    public static void main(String[] args) throws ISOException {

//        String CVV = " 712";
//        String CVV2 = " 123";
//        System.out.println("Integer.valueOf(CVV) = " + Integer.valueOf(CVV.trim()));
//        System.out.println("Integer.valueOf(CVV2) = " + Integer.valueOf(CVV2.trim()));
        String a = "1";
        a = ISOUtil.zeropad(a,3);
        System.out.println("a = " + a);

        //msg.set(48, "00" + ISOUtil.zeropad(cardHolderSecurity.getCvv2(), 4) + subProcessCode);
    }
}

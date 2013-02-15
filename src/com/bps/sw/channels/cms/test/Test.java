package com.bps.sw.channels.cms.test;

import com.bps.security.channel.SecurityHandler;
import org.jpos.iso.ISOUtil;

/**
 * Created by IntelliJ IDEA.
 * User: bayandor
 * Date: May 16, 2012
 * Time: 9:37:24 AM
 * To change this template use File | Settings | File Templates.
 */
public class Test {
    public static void main(String[] args) {
        testCreatePinBlock();
    }

    public static void testCreatePinBlock() {

        SecurityHandler securityHandler = new SecurityHandler();
        long pan = 6104337800001271L;
        String pin1 = null;
        try {
            pin1 = securityHandler.getPinBlock("3213", pan, (short) 0);
            String pin2 = securityHandler.getPinBlock("55555", pan, (short) 0);
            System.out.println("pan = " + pan + ", pin1 = " + pin1 + ", pin2 = " + pin2);

            String pinBlock = securityHandler.encryptPinBlock(ISOUtil.hex2byte("EA1465867534FA1C"), ISOUtil.hex2byte("59e3677dca6c9a47"), (short) 0);
            System.out.println("pinBlock = " + pinBlock);
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }
}

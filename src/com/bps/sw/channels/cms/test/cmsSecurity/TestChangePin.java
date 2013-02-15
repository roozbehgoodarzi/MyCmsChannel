package com.bps.sw.channels.cms.test.cmsSecurity;

import com.bps.sw.core.SwitchEnums;
import com.bps.sw.core.interfaces.ICmsInterface;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by IntelliJ IDEA.
 * User: GOODARZI
 * Date: 2/9/12
 * Time: 1:39 PM
 * To change this template use File | Settings | File Templates.
 */
public class TestChangePin {

    public static void main(String[] args) {
        String configFile = "cms_client.xml";
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext(configFile);
        System.out.println("-----Cms client Started-----");
        ICmsInterface remoteService = (ICmsInterface) applicationContext.getBean("cmsSecurityService");
        Integer result = -1;
        try {
//            String pan = "6104337186074710";
//            String oldEncryptedPinBlock = "85beaf1d0f6adb7d";
//            String pvv = "7440";
//            String newEncryptedPinBlock = "90859619f239d98c";
            String pan = "6104332108732941";
            String oldEncryptedPinBlock = "EB86069A4F185D2C";
            String pvv = "6976";
            String newEncryptedPinBlock = "EB86069A4F185D2C";
            result = remoteService.changePin1(pan, 103L, oldEncryptedPinBlock, pvv, newEncryptedPinBlock, (short)15, "");
            System.out.println("Response Code: " + result);
        } catch (Exception e) {
            System.out.println("e.toString() = " + e.toString());
        }

    }
}

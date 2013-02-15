package com.bps.sw.channels.cms.test.cmsSecurity;

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
public class TestVerifyPin {

    public static void main(String[] args) {
        String pan = "6104337800000844";
        String strEncPinBlock = "A8AD96A12261E558";
//        String strEncPinBlock = "AD816E5AA492548D";
        String pvvBytes = "26980000000004";
        String configFile = "cms_client.xml";
        ApplicationContext applicationContext= new ClassPathXmlApplicationContext(configFile);
        System.out.println("-----Cms client Started-----");
        ICmsInterface remoteService = (ICmsInterface) applicationContext.getBean("cmsSecurityService");
        Integer result = -1;
        try {
            result = remoteService.verifyPin(pan, strEncPinBlock, pvvBytes, "");
            System.out.println("Response Code: " + result);
        } catch (Exception e) {
            System.out.println("e.toString() = " + e.toString());
        }

    }
}

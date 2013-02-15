package com.bps.sw.channels.cms.test.cmsHsm;

import com.bps.sw.core.interfaces.IHsmJniLib;
import com.bps.sw.model.entity.security.CVV2RmiResult;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by IntelliJ IDEA.
 * User: GOODARZI
 * Date: 2/9/12
 * Time: 1:39 PM
 * To change this template use File | Settings | File Templates.
 */
public class TestGetCVV2 {

    public static void main(String[] args) {
/*
        !!! pin : 1107 !!!
        !!! strEncPinBlock : AD816E59F492548D !!!
        !!! pvvBytes : 009600000000 !!!
        !!! pan : 6104332108732941!!!
*/


        String pan = "6104331000000068";
        String configFile = "cms_client.xml";
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext(configFile);
        System.out.println("-----Cms client Started-----");
        IHsmJniLib remoteService = (IHsmJniLib) applicationContext.getBean("cmsHsmService");
        try {
            CVV2RmiResult cvv2RmiResult = remoteService.generateCVV2(pan.toString(), "ip");
            System.out.println("cvv2: " + cvv2RmiResult.getCvv2());
        } catch (Exception e) {
            System.out.println("e.toString() = " + e.toString());
        }

    }
}

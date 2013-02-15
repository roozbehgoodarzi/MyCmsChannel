package com.bps.sw.channels.cms.test.cmsHsm;

import com.bps.sw.core.interfaces.IHsmJniLib;
import com.bps.sw.model.entity.security.RmiResult;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by IntelliJ IDEA.
 * User: GOODARZI
 * Date: 2/9/12
 * Time: 1:39 PM
 * To change this template use File | Settings | File Templates.
 */
public class TestVerifyPinHsm {

    public static void main(String[] args) {
        String pan = "6104337800000844";
        String strEncPinBlock = "A8AD96A12261E558";
        String pvvBytes = "26980000000004";
        String configFile = "cms_client.xml";
        String ppk = "123";
        ApplicationContext applicationContext= new ClassPathXmlApplicationContext(configFile);
        System.out.println("-----Cms client Started-----");
        IHsmJniLib remoteService = (IHsmJniLib) applicationContext.getBean("cmsHsmService");
        RmiResult result = null;
        try {
            result = remoteService.verifyPin(pan, strEncPinBlock, pvvBytes, ppk, "");
            System.out.println("Response Code: " + result.getRetValue());
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("e.toString() = " + e.toString());
        }

    }
}

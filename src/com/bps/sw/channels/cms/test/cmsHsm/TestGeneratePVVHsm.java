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
public class TestGeneratePVVHsm {

    public static void main(String[] args) {
        String pan = "6104338000000444";
        String strEncPinBlock = "B36DD79EA4815B63";
        String pvvBytes = "7487";
        String configFile = "cms_client.xml";
        String ppk = "123";
        ApplicationContext applicationContext= new ClassPathXmlApplicationContext(configFile);
        System.out.println("-----Cms client Started-----");
        IHsmJniLib remoteService = (IHsmJniLib) applicationContext.getBean("cmsHsmService");
        RmiResult result = null;
        try {
            result = remoteService.generatePVV(pan, strEncPinBlock,ppk , pvvBytes);
            System.out.println("Response Code: " + result.getRetValue());
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("e.toString() = " + e.toString());
        }

    }
}

package com.bps.sw.channels.cms.test;

import com.bps.sw.core.interfaces.ICmsInterface;
import com.bps.sw.model.entity.security.PinInfo;
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
public class TestCmsSecurity {

    public static void main(String[] args) {
        String pan = "6104332108732941";
        String configFile = "cms_client.xml";
        ApplicationContext applicationContext= new ClassPathXmlApplicationContext(configFile);
        System.out.println("-----Cms client Started-----");
        ICmsInterface remoteService = (ICmsInterface) applicationContext.getBean("cmsSecurityService");
        try {

            System.out.println("Before");
            Integer rmiResult  = remoteService.verifyPin("6104333990008432", "AF0C230DF4926F54", "80320000000004", "");

            System.out.println("Response Code: " + rmiResult);
        } catch (Exception e) {
            System.out.println("e.toString() = " + e.toString());
        }
    }

    public static void main2(String[] args) {
        String pan = "6104332108732941";
        String configFile = "cms_client.xml";
        ApplicationContext applicationContext= new ClassPathXmlApplicationContext(configFile);
        System.out.println("-----Cms client Started-----");
        ICmsInterface remoteService = (ICmsInterface) applicationContext.getBean("cmsSecurityService");
        try {
            String s = "010001BEB43E3096B665B61747FD46EFA0BF0B4CA9612DB05EFEF9DB52741013EB2978596AD0A7F8C9AF5E16AF8D187D0C20F366A2C470CFCD1E078C416F4AB13F31DCA495F073CB046846C5CA8276BB7222BD47D06014282452A5B3240C72A0DC6817579F3D11362141A5FC0AB0F833F7663A1F14053FD9B6F9D212E03EA6FA05F78F";
            PinInfo pinInfo = remoteService.issuePin4IssueCenter(pan, 103L, 5, s, "");
            System.out.println("Response Code: " + pinInfo.getResponseCode());
            System.out.println("EncryptedPin: " + pinInfo.getEncryptedPin());
        } catch (Exception e) {
            System.out.println("e.toString() = " + e.toString());
        }

    }
}

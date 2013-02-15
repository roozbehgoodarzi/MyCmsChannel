package com.bps.sw.channels.cms.test.cmsSecurity;

import com.bps.sw.core.interfaces.ICmsInterface;
import com.bps.sw.model.entity.security.PinInfo;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by IntelliJ IDEA.
 * User: GOODARZI
 * Date: 2/9/12
 * Time: 1:39 PM
 * To change this template use File | Settings | File Templates.
 */
public class TestIssuePin4IssueCenter {

    public static void main(String[] args) {
//        String pan = "6104331000000068";
        String pan = "6104337800001271";
        String configFile = "cms_client.xml";
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext(configFile);
        System.out.println("-----Cms client Started-----");
        ICmsInterface remoteService = (ICmsInterface) applicationContext.getBean("cmsSecurityService");
        String s = "010001E9ED497046C1E27D50F70F6F9A9EDF7EAAF4D13E7C06F785DFD50F59B3CCC723A218D7B6EF922024CB168825AF09812CD3AFD5B2FD2E53AEC83B9471A1A17C67810AEA2764C8FA1A589E49D6D14BE169090CC66ED38513DAFCCDA5146DBE3F3E70A72F84DD669A46542FD366548AE7A111238F28637B4D7B128E832EB7B79B1D";
//        String s = "010001E9AAAA7046C1E27D50F70F6F9A9EDF7EAAF4D13E7C06F785DFD50F59B3CCC723A218D7B6EF922024CB168825AF09812CD3AFD5B2FD2E53AEC83B9471A1A17C67810AEA2764C8FA1A589E49D6D14BE169090CC66ED38513DAFCCDA5146DBE3F3E70A72F84DD669A46542FD366548AE7A111238F28637B4D7B128E832EB7B79B1D";

        try {
            PinInfo pinInfo = remoteService.issuePin4IssueCenter(pan, 103L, 0, s, "");
            System.out.println("Response Code: " + pinInfo.getResponseCode());
            System.out.println("EncryptedPin: " + pinInfo.getEncryptedPin());
        } catch (Exception e) {
            System.out.println("e.toString() = " + e.toString());
        }

    }
}

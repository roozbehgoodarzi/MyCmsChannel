package com.bps.sw.channels.cms.test.cmsHsm;

import com.bps.sw.core.interfaces.IHsmJniLib;
import com.bps.sw.model.entity.security.IssuePinRmiResult;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by IntelliJ IDEA.
 * User: GOODARZI
 * Date: 2/9/12
 * Time: 1:39 PM
 * To change this template use File | Settings | File Templates.
 */
public class TestIssuePinHsm {

    public static void main(String[] args) {

//        String pan = "6104331000000068";
//        String pan = "6104337800000844";
        String pan = "6104333800000161";
        String configFile = "cms_client.xml";
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext(configFile);
        System.out.println("-----Cms client Started-----");
        IHsmJniLib remoteService = (IHsmJniLib) applicationContext.getBean("cmsHsmService");
//        String s = "010001E9ED497046C1E27D50F70F6F9A9EDF7EAAF4D13E7C06F785DFD50F59B3CCC723A218D7B6EF922024CB168825AF09812CD3AFD5B2FD2E53AEC83B9471A1A17C67810AEA2764C8FA1A589E49D6D14BE169090CC66ED38513DAFCCDA5146DBE3F3E70A72F84DD669A46542FD366548AE7A111238F28637B4D7B128E832EB7B79B1D";
        String s = "010001BAED11021218380C75EAE7BEDF71B204B5AD7FEB6A8D9D0B8615B96B126D5A550D88BEAA34DD866309F10E57A03BC15B66182C91722203A713A638F05D29534CEBC3587BB7C92B8964B2FF5FD5602E508B26C701485717F23C2970F552D277BE682FA3CBD3DDD88BDBAF992FFCE31F3A673EC45B2F431FE7EF5CB213CE88B745";
        try {
            IssuePinRmiResult issuePinRmiResult = remoteService.issuePin(pan, s.substring(6, s.length()), s.substring(0, 6), "172.20.120.224","");
            System.out.println("ResCode: " + issuePinRmiResult.getResCode());
            System.out.println("Pvv: " + issuePinRmiResult.getPvv());
            System.out.println("AsynchEncPin: " + issuePinRmiResult.getAsynchEncPin());
        } catch (Exception e) {
            System.out.println("e.toString() = " + e.toString());
        }

    }
}

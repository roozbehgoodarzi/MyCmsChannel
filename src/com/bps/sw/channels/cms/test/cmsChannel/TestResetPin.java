package com.bps.sw.channels.cms.test.cmsChannel;

import com.bps.sw.core.SwitchConstants;
import com.bps.sw.core.interfaces.IRemoteService;
import com.bps.sw.model.entity.CardHolderSecurity;
import com.bps.sw.model.entity.TPTransaction;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Roozbeh Goodarzi
 * Date: 10/30/12
 * Time: 11:24 AM
 * To change this template use File | Settings | File Templates.
 */
public class TestResetPin {
       public static void main(String[] args) {
        HashMap map = new HashMap();

        BigDecimal internalReferenceId = BigDecimal.valueOf(1234);
        String pan = "6104333800000454";
        String strEncPinBlock = "043250C87FFFFED8";

        TPTransaction tpTransaction = new TPTransaction();
        tpTransaction.setPan(6104333800000454l);
        tpTransaction.setTerminalType(SwitchConstants.TERMINAL_TYPE_PINPAD);
        tpTransaction.setInternalReferenceId(internalReferenceId);
        tpTransaction.setServiceGroupId(Short.valueOf("61"));

        CardHolderSecurity cardHolderSecurity = new CardHolderSecurity();
        cardHolderSecurity.setSecurityType(SwitchConstants.SECURITY_BASED_ON_PIN1);
        cardHolderSecurity.setPin("043250C87FFFFED8");
        cardHolderSecurity.setCvv2("66");
        cardHolderSecurity.setTrack2("6104333800000454=13101007102063415910");
        cardHolderSecurity.setExpirationDate("139207");
        cardHolderSecurity.setPan(6104333800000454l);
        cardHolderSecurity.setNewPin("043250C87FFFFED8");

        map.put(SwitchConstants.INTERNAL_REFERENCE_NUMBER, internalReferenceId);
        map.put(SwitchConstants.TP_TRANSACTION_OBJECT, tpTransaction);
        map.put(SwitchConstants.CARDHOLDER_SECURITY_OBJECT, cardHolderSecurity);

        String configFile = "cms_client.xml";
        ApplicationContext applicationContext= new ClassPathXmlApplicationContext(configFile);
        System.out.println("-----Cms client Started-----");
        IRemoteService remoteService = (IRemoteService) applicationContext.getBean("cmsChannelService");

        try {
            Map outMap = remoteService.synchSend(map);
            TPTransaction transaction = (TPTransaction) outMap.get(SwitchConstants.TP_TRANSACTION_OBJECT);
            System.out.println("Response Code: " + transaction.getResponseCode());

        } catch (Exception e) {
            System.out.println("e.toString() = " + e.toString());
        }

    }
}

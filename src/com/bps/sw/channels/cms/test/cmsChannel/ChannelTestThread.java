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
 * User: GOODARZI
 * Date: 5/5/12
 * Time: 11:07 AM
 * To change this template use File | Settings | File Templates.
 */
public class ChannelTestThread implements Runnable{
    public void run() {
        HashMap map = new HashMap();

              BigDecimal internalReferenceId = BigDecimal.valueOf(1234);
//        String pan = "6104332108732941";
              String pan = "6104337800000760";
//        String strEncPinBlock = "AD816E59F492548D";
              String strEncPinBlock = "AD816E59F492548D";
              String pvvBytes = "0096";
              String cvv2 = "794";

              TPTransaction tpTransaction = new TPTransaction();
              tpTransaction.setPan(6104337800000760l);
              tpTransaction.setTerminalType(SwitchConstants.TERMINAL_TYPE_VPOS);
              tpTransaction.setInternalReferenceId(internalReferenceId);
              tpTransaction.setServiceGroupId(Short.valueOf("1"));

              CardHolderSecurity cardHolderSecurity = new CardHolderSecurity();
              cardHolderSecurity.setSecurityType(SwitchConstants.SECURITY_BASED_ON_PIN1);
              cardHolderSecurity.setPin("AD816E59F492548D");
              cardHolderSecurity.setCvv2("794");

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

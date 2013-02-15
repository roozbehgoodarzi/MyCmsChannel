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
 * Date: 2/9/12
 * Time: 1:39 PM
 * To change this template use File | Settings | File Templates.
 */
public class TestChangePin {

    public static void main(String[] args) {
        HashMap map = new HashMap();

        BigDecimal internalReferenceId = BigDecimal.valueOf(1234);

        TPTransaction tpTransaction = new TPTransaction();
        tpTransaction.setPan(6104332108732941L);
        tpTransaction.setTerminalType(SwitchConstants.TERMINAL_TYPE_POS);
        tpTransaction.setInternalReferenceId(internalReferenceId);
        tpTransaction.setServiceGroupId(Short.valueOf("50"));

        CardHolderSecurity cardHolderSecurity = new CardHolderSecurity();
        cardHolderSecurity.setSecurityType(SwitchConstants.SECURITY_BASED_ON_PIN1);
//        cardHolderSecurity.setPin("EB86FF9A4F185D2C");
//        cardHolderSecurity.setPin("EB86069A4F185D2C");
        cardHolderSecurity.setPin("07ed15697f0999d4");
        cardHolderSecurity.setNewPin("07ed15697f0999d4");
        //cardHolderSecurity.setPin("04235acdef78cd6b");
//        cardHolderSecurity.setCvv2("6976");

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

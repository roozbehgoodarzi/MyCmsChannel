package com.bps.sw.channels.cms.handlers;

import com.bps.sw.core.SwitchConstants;
import com.bps.sw.core.interfaces.IHandler;
import com.bps.sw.core.interfaces.IRemoteService;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Goodarzi
 * Date: 8/26/12
 * Time: 10:37 AM
 * To change this template use File | Settings | File Templates.
 */
public class SendToNeginChannelHandler extends IHandler{
       IRemoteService iRemoteService;

    public void setIRemote(IRemoteService iRemoteService) {
        this.iRemoteService = iRemoteService;
    }

    public void process(Map map) throws Exception {
        log.debug("---- inside " + getClass() + " --" + map.get(SwitchConstants.INTERNAL_REFERENCE_NUMBER));
        HashMap hashMap = new HashMap(map);
        hashMap.remove(SwitchConstants.PAN_OBJECT);
        try {
            hashMap = iRemoteService.synchSend(hashMap);
            map.putAll(hashMap);
        } catch (Exception e) {
            throw e;
        }
    }
}

package com.bps.sw.channels.cms.handlers;

import com.bps.sw.core.SwitchConstants;
import com.bps.sw.core.interfaces.IHandler;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Goodarzi
 * Date: 9/5/12
 * Time: 1:58 PM
 * To change this template use File | Settings | File Templates.
 */
public class SendToNeginAndUpdatePin2Handler extends IHandler {
       public void process(Map map) throws Exception {
//        if(log.isDebugEnabled())
            log.info("---- inside " + getClass() + " --" + map.get(SwitchConstants.INTERNAL_REFERENCE_NUMBER));
        processChainHandler(map);
    }
}

package com.bps.sw.channels.cms.handlers;

import com.bps.sw.core.SwitchConstants;
import com.bps.sw.core.interfaces.IHandler;

import java.util.Map;

public class ResetPinHandler extends IHandler {
    public void process(Map map) throws Exception {
//        if(log.isDebugEnabled())
            log.info("---- inside " + getClass() + " --" + map.get(SwitchConstants.INTERNAL_REFERENCE_NUMBER));
        processChainHandler(map);
    }
}
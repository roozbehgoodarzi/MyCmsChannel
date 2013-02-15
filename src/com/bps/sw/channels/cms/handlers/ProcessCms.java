package com.bps.sw.channels.cms.handlers;

import com.bps.sw.core.SwitchConstants;
import com.bps.sw.core.interfaces.IHandler;
import com.bps.sw.core.interfaces.IRemoteService;

import java.util.HashMap;
import java.util.Map;

public class ProcessCms extends IHandler implements IRemoteService {
    public void process(Map map) throws Exception {
        log.info("---- inside " + getClass() + " --" + map.get(SwitchConstants.INTERNAL_REFERENCE_NUMBER));
        processChainHandler(map);
    }

    public HashMap synchSend(HashMap hashMap) throws Exception {
        process(hashMap);
        return hashMap;
    }

    public void test(String s) throws Exception {
    }
}
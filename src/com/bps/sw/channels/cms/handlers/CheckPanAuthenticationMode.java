package com.bps.sw.channels.cms.handlers;

import com.bps.sw.channels.cms.util.CmsConstants;
import com.bps.sw.core.SwitchConstants;
import com.bps.sw.core.exp.SecurityViolationException;
import com.bps.sw.core.interfaces.ICmsInterface;
import com.bps.sw.core.interfaces.IHandler;
import com.bps.sw.model.entity.Pan;
import com.bps.sw.model.facade.CmsFacade;
import com.bps.sw.model.facade.SwitchFacade;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: GOODARZI
 * Date: 4/14/12
 * Time: 12:00 PM
 * To change this template use File | Settings | File Templates.
 */
public class CheckPanAuthenticationMode extends IHandler {
    CmsFacade cmsFacade;
    SwitchFacade facade;
    ICmsInterface remoteHsmService;

    public void setCmsFacade(CmsFacade cmsFacade) {
        this.cmsFacade = cmsFacade;
    }

    public void setFacade(SwitchFacade facade) {
        this.facade = facade;
    }

    public void setRemoteHsmService(ICmsInterface remoteHsmService) {
        this.remoteHsmService = remoteHsmService;
    }


    @Override
    public void process(Map map) throws Exception {
        if (log.isDebugEnabled())
            log.debug("---- inside " + getClass() + " --" + map.get(SwitchConstants.INTERNAL_REFERENCE_NUMBER));

        Pan panObject = (Pan) map.get(SwitchConstants.PAN_OBJECT);

        Short authenticationMode = panObject.getAuthenticationMode();
        if (authenticationMode.equals(CmsConstants.AUTHENTICATION_MUST_NOT_BE_CHECKED))
           throw new SecurityViolationException(SwitchConstants.SOURCE_ID_CMS_CHANNEL);
    }
}

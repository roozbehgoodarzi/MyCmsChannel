package com.bps.sw.channels.cms;

import com.bps.sw.core.SpringFacade;

public class CmsMain {
    public static void main(String[] args) {
        String configFile = "cms_config.xml";
        SpringFacade.initApplicationContext(configFile);
        System.out.println(" ------ CMS Channel Started -------");
    }

}

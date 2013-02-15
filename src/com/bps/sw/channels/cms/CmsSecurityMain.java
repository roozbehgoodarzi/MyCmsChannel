package com.bps.sw.channels.cms;

import com.bps.sw.core.SpringFacade;

/**
 * Created by IntelliJ IDEA.
 * User: GOODARZI
 * Date: 5/19/12
 * Time: 11:23 AM
 * To change this template use File | Settings | File Templates.
 */
public class CmsSecurityMain {
    public static void main(String[] args) {
          String configFile = "cms_security_config.xml";
          SpringFacade.initApplicationContext(configFile);
          System.out.println(" ------ CMS Security Channel Started -------");
      }

}

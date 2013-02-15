package com.bps.sw.channels.cms.test.cmsChannel;

/**
 * Created by IntelliJ IDEA.
 * User: GOODARZI
 * Date: 5/5/12
 * Time: 11:49 AM
 * To change this template use File | Settings | File Templates.
 */
public class ChannelThreadRunner {
    public static void main(String[] args) {
        for (int i = 0; i < 30; i++) {
            ChannelTestThread thread = new ChannelTestThread();
            new Thread(thread).start();
        }
    }
}

/*
 * Copyright (c) 2008 Jimmy Mitchener <jcm@packetpan.org>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. The name of the author may not be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR "AS IS" AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.github.amnotbot;

import com.github.amnotbot.config.BotConfiguration;

public class BotConstants
{

    private final String DEFAULT_NICK = "amcoolbot";
    /** Prefix for server level messages */
    private final String SERVER_PFX = "-!-";
    /** Prefix for application level messages */
    private final String APP_PFX = "***";
    private final int IRC_PORTS[] = {6667, 6666, 6660, 6661, 6662,
        6663, 6664, 6665, 6668, 6669};
    private static BotConstants botConstants = null;
    private String nick;

    protected BotConstants()
    {
        this.nick = null;
    }

    public static BotConstants getBotConstants()
    {
        if (botConstants == null) {
            botConstants = new BotConstants();
        }

        return botConstants;
    }

    public String getNick()
    {
        if (this.nick != null) return this.nick;

        try {
            this.nick = BotConfiguration.getConfig().getString("nick");
        } catch (Exception e) {
            BotLogger.getDebugLogger().debug(e);
        }

        if (this.nick == null) {
            this.nick = this.DEFAULT_NICK;
        }

        return this.nick;
    }

    public int[] getIrcPorts()
    {
        return this.IRC_PORTS;
    }

    public String getServerPFX()
    {
        return this.SERVER_PFX;
    }

    public String getAppPFX()
    {
        return this.APP_PFX;
    }
}

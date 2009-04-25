/*
 * Copyright (c) 2007 Jimmy Mitchener <jimmy.mitchener@gmail.com>
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
package org.knix.amnotbot;

import java.util.List;

import org.knix.amnotbot.config.BotConfiguration;
import org.schwering.irc.lib.IRCEventListener;
import org.schwering.irc.lib.IRCModeParser;
import org.schwering.irc.lib.IRCUser;
import com.yahoo.search.SearchClient;

/**
 * Event handler for MainBot. This is the real workhorse.
 * @author Jimmy Mitchener
 *
 * 22-10-2007 gresco Implemented the "Command Pattern" for commands. 
 */
public class BotListener implements IRCEventListener
{
    private BotConnection con;
    private List<String> channels;
    private SpamDetector spamDetector;
    /** Stores all Commands **/
    private MacroCommand macroCommand;

    /**
     * Create a new BotListener object
     * @param con IRC Connection we're handling
     * @param channels Channels to join on connect
     */
    public BotListener(BotConnection con, List<String> channels)
    {
        this.con = con;
        this.channels = channels;
        this.spamDetector = new SpamDetector(channels);
        this.macroCommand = new MacroCommand();

        if (BotConfiguration.getConfig().getBoolean("delicious_enabled")) {
            this.macroCommand.add(new DeliciousCommand(true));
        }

        DictHelper dictHelper = new DictHelper(con);
        this.macroCommand.add(new DictCommand(dictHelper));
        this.macroCommand.add(new SpellCommand(dictHelper));

        this.macroCommand.add(new QurlRequestCommand());
        this.macroCommand.add(new GoogleCommand());
        this.macroCommand.add(new GoogleBookSearchCommand());
        SearchClient yahooClient = new SearchClient("G7RklHzV34Gs_AYiBU0xA4wak1J3plPRonFhFfwJEeXMVP4PrpvwiflgxXa4uw--");
        this.macroCommand.add(new YahooWebSearchCommand(yahooClient));
        this.macroCommand.add(new YahooNewsSearchCommand(yahooClient));
        this.macroCommand.add(new GoogleYouTubeCommand());
        this.macroCommand.add(new URLSearchCommand(yahooClient));
        this.macroCommand.add(new WordsCommand());
        this.macroCommand.add(new LinesCommand());
        this.macroCommand.add(new QuoteCommand());

        HelpCommand helpCommand = new HelpCommand();
        helpCommand.addCommands(this.macroCommand.getCommands());
        this.macroCommand.add(helpCommand);
    }

    public void onPrivmsg(String target, IRCUser user, String msg)
    {
        con.print(target, user.getNick() + "> " + msg);

        if (con.isSilent()) return;

        boolean targetIsMe = false;
        if ( target.equals(this.con.getNick()) ) {
            target = user.getNick();
            targetIsMe = true;
        }

        if (this.macroCommand.matches(msg)) {
            if (!targetIsMe) {
                if (this.spamDetector.checkForSpam(target, user)) {
                    BotLogger.getDebugLogger().debug("Spam Detected!");
                    return;
                }
            }

            this.macroCommand.execute(this.con, target, user, msg);
        }
    }

    public void onDisconnected()
    {
        con.print(BotConstants.getBotConstants().getAppPFX() + " DISCONNECTED!");
    }

    public void onError(int num, String msg)
    {
        con.print(BotConstants.getBotConstants().getServerPFX() +
                " ERROR #" + num + " !!!!!");
        con.print("\t" + msg);

        switch (num) {
            case 432:
            case 433:
                con.alternateNick();
                break;
        }
    }

    public void onError(String msg)
    {
        con.print(BotConstants.getBotConstants().getServerPFX() + " ERROR!!!!");
        con.print("\t" + msg);
    }

    public void onInvite(String chan, IRCUser user, String pNick)
    {
        con.print(chan, BotConstants.getBotConstants().getServerPFX() +
                " " + pNick + " invited " +
                ((pNick.compareTo(con.getNick()) == 0) ? "you" : pNick) +
                " to join " + chan);
    }

    public void onJoin(String chan, IRCUser user)
    {
        con.print(chan, BotConstants.getBotConstants().getServerPFX() +
                " " + user.getNick() +
                " [" + user.getHost() + "] has joined " + chan);
    }

    public void onKick(String chan, IRCUser user, String pNick, String msg)
    {
        this.con.print(chan, BotConstants.getBotConstants().getServerPFX() +
                " " + user.getNick() + " [" + user.getHost() + "] has kicked " +
                pNick + " from " + chan + " with message [" + msg + "]");

        if ( pNick.equals(this.con.getNick()) ) {
            if (BotConfiguration.getConfig().getBoolean("auto_rejoin")) {
                this.con.doJoin(chan);
            }
        }
    }

    public void onNick(IRCUser nick, String newNick)
    {
        this.con.print(BotConstants.getBotConstants().getServerPFX() +
                " " + nick.getNick() + " is now known as " + newNick);
    }

    public void onNotice(String target, IRCUser user, String msg)
    {
        this.con.print(BotConstants.getBotConstants().getServerPFX() +
                " NOTICE " + user.getNick() + ": " + msg);

        if (user.getNick() == null) {
            return;
        }

        String nickserv = BotConfiguration.getConfig().getString("nickserv");
        boolean nickservTalking = user.getNick().equalsIgnoreCase(nickserv);

        if (nickservTalking) {
            if (BotConfiguration.getConfig().getBoolean("nickserv_enabled")) {
                String identifyMsg = new String("IDENTIFY");
                if (msg.toLowerCase().contains(identifyMsg.toLowerCase())) {
                    this.con.doPrivmsg(user.getNick(),
                            "IDENTIFY " +
                            BotConfiguration.getConfig().getString("nickserv_password"));
                }
            }
        }
    }

    public void onPart(String chan, IRCUser user, String msg)
    {
        this.con.print(chan, BotConstants.getBotConstants().getServerPFX() +
                " " + user.getNick() + " [" + user.getHost() + "] has left " +
                chan + " [" + msg + "]");
    }

    public void onPing(String ping)
    {
        this.con.print(BotConstants.getBotConstants().getServerPFX() +
                " PING: " + ping);
    }

    public void onQuit(IRCUser user, String msg)
    {
        this.con.print(BotConstants.getBotConstants().getServerPFX() +
                " " + user + " [" + user.getHost() + "] has quit [" + msg + "]");
    }

    /**
     * Join channels supplied on init once registered.
     */
    public void onRegistered()
    {
        this.con.print(BotConstants.getBotConstants().getServerPFX() +
                " SUCCESS: " + this.con.getHost() + " connection registered");

        for (String channel : channels) {
            this.con.doJoin(channel);
        }
    }

    public void onReply(int num, String value, String msg)
    {
    }

    public void onMode(String chan, IRCUser user, IRCModeParser mParser)
    {
        /* 
         * TODO - actually use the parser instead of calling getLine()
         */
        this.con.print(chan,
                BotConstants.getBotConstants().getServerPFX() +
                " mode/" + chan + " [" + mParser.getLine() + "] " +
                "by " + user.getNick());
    }

    public void onMode(IRCUser user, String pNick, String mode)
    {
        this.con.print(user.getNick() + " has changed user mode for " +
                (user.getNick().equals(pNick) ? "himself" : pNick) +
                " to " + mode);
    }

    public void onTopic(String chan, IRCUser user, String topic)
    {
        this.con.print(chan, user.getNick() +
                " has changed the topic to: " + topic);
    }

    public void unknown(String pfx, String cmd, String middle, String end)
    {
        this.con.print(BotConstants.getBotConstants().getAppPFX() +
                " Received UNKNOWN Event: " + cmd);
        this.con.print("\tprefix: '" + pfx + "'");
        this.con.print("\tmiddle: '" + middle + "'");
        this.con.print("\tend:    '" + end + "'");
    }
}

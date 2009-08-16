package org.knix.amnotbot;

import java.io.IOException;

/**
 *
 * @author gpoppino
 */
public class DummyConnection implements BotConnection
{
    private String output;

    public DummyConnection()
    {
        this.output = null;
    }

    @Override
    public void doPrivmsg(String target, String msg)
    {
        this.output = msg;
    }

    public String getOutput()
    {
        return this.output;
    }

    @Override
    public void connect() throws IOException
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void doNick(String nick)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void doQuit()
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void doJoin(String room)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isConnected()
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void print(String msg)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void print(String target, String msg)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setTimeout(int millis)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setEncoding(String encoding)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setBotLogger(BotLogger logger)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public BotLogger getBotLogger()
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getHost()
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getNick()
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}

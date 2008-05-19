package org.knix.amnotbot;

import org.schwering.irc.lib.IRCUser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 * User: gresco
 * Date: Oct 20, 2007
 * Time: 2:43:47 PM
 */
public abstract class AmnotbotCommandImp implements AmnotbotCommand {

	private Pattern commandPattern = null;
	private String commandKeywords = null;
	private Matcher m = null;

	private boolean initialized = false;

	public AmnotbotCommandImp(String regexp, String keywords) 
	{
		if (regexp != null) {
			this.commandPattern = Pattern.compile(regexp, Pattern.CASE_INSENSITIVE);
			this.setKeywords(keywords);

			this.initialized = true;
		}
	}

	public boolean matches(String msg)
	{
		if (!this.initialized)
			return false;

		this.m = this.commandPattern.matcher(msg);
		if (this.m.find()) {
			return true;
		}

		return false;
	}

	public String getGroup(int n)
	{
		if (!this.initialized)
			return null;

		return this.m.group(n);
	}

	public String help() 
	{
		return null;
	}

	public String getKeywords()
	{
		return this.commandKeywords;
	}

	public void setKeywords(String keywords)
	{
		this.commandKeywords = keywords;
	}
}
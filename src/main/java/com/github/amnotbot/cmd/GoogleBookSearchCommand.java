package com.github.amnotbot.cmd;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;
import org.apache.commons.configuration.Configuration;

import com.github.amnotbot.BotCommand;
import com.github.amnotbot.BotMessage;
import com.github.amnotbot.cmd.utils.Utf8ResourceBundle;
import com.github.amnotbot.config.BotConfiguration;

public class GoogleBookSearchCommand implements BotCommand
{

    public void execute(BotMessage message)
    {
        new GoogleSearchImp(
                GoogleSearch.searchType.BOOKS_SEARCH,
                new GoogleResultOutputBooksStrategy(),
                message).run();
    }

    public String help()
    {
        String msg = new String();
        Locale currentLocale;
        ResourceBundle helpMessage;

        currentLocale = new Locale(
                BotConfiguration.getConfig().getString("language"),
                BotConfiguration.getConfig().getString("country"));
        helpMessage = Utf8ResourceBundle.getBundle(
                "GoogleBundle", currentLocale);

        Configuration cmdConfig = BotConfiguration.getCommandsConfig();
        String cmd = cmdConfig.getString("GoogleBookSearchCommand");

        Object[] messageArguments = {
            BotConfiguration.getConfig().getString("command_trigger"),
            cmd,
            helpMessage.getString("book_short_description"),
            helpMessage.getString("parameters"),
            helpMessage.getString("search_term"),
            helpMessage.getString("example")
        };

        MessageFormat formatter = new MessageFormat("");
        formatter.setLocale(currentLocale);
        formatter.applyPattern(helpMessage.getString("template"));

        String output = formatter.format(messageArguments);
        return output;
    }

}

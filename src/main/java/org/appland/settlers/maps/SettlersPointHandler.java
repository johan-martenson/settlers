package org.appland.settlers.maps;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.OptionDef;
import org.kohsuke.args4j.spi.Messages;
import org.kohsuke.args4j.spi.OneArgumentOptionHandler;
import org.kohsuke.args4j.spi.Setter;

import java.awt.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SettlersPointHandler extends OneArgumentOptionHandler<Point> {
    private static final Pattern PATTERN = Pattern.compile("([0-9]+),([0-9]+)", Pattern.CASE_INSENSITIVE);

    public SettlersPointHandler(CmdLineParser parser, OptionDef option, Setter<? super Point> setter) {
        super(parser, option, setter);
    }

    @Override
    protected Point parse(String argument) throws CmdLineException {
        Matcher matcher = PATTERN.matcher(argument);
        if (!matcher.matches()) {
            throw misformattedArgumentException(argument);
        }
        return new Point(Integer.parseInt(matcher.group(1)), Integer.parseInt(matcher.group(2)));
    }

    private CmdLineException misformattedArgumentException(String token) {
        return new CmdLineException(owner, Messages.ILLEGAL_OPERAND, option.toString(), token);
    }

    @Override
    public String getDefaultMetaVariable() {
        return "x,y";
    }
}


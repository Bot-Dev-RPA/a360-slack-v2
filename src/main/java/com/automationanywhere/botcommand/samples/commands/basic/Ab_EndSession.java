package com.automationanywhere.botcommand.samples.commands.basic;
import com.automationanywhere.commandsdk.annotations.*;
import com.automationanywhere.commandsdk.annotations.rules.NotEmpty;
import com.automationanywhere.commandsdk.annotations.rules.NotEmpty;
import com.automationanywhere.commandsdk.i18n.Messages;
import com.automationanywhere.commandsdk.i18n.MessagesFactory;
import com.automationanywhere.commandsdk.annotations.BotCommand;
import com.automationanywhere.commandsdk.annotations.CommandPkg;
import com.automationanywhere.commandsdk.annotations.Execute;
import com.automationanywhere.commandsdk.annotations.Idx;
import com.automationanywhere.commandsdk.annotations.Pkg;
import com.automationanywhere.commandsdk.annotations.Sessions;

import java.util.Map;

import static com.automationanywhere.commandsdk.model.AttributeType.TEXT;
import static com.automationanywhere.commandsdk.model.DataType.STRING;

/**
 * @author James Dickson
 *
 */

@BotCommand
@CommandPkg(label = "End Session",
        name = "EndSnowSession",
        description = "Session End",
        icon = "SLACK.svg",
        node_label = "End Session {{sessionName}}",
        group_label = "Admin",
        comment = true
        )

public class Ab_EndSession {

    @Sessions
    private Map<String, Object> sessions;

    @Execute
    public void end(@Idx(index = "1", type = TEXT) @Pkg(label = "Session name", default_value_type = STRING, default_value = "Default") @NotEmpty String sessionName){

        sessions.remove(sessionName);
    }
    public void setSessions(Map<String, Object> sessions) {
        this.sessions = sessions;
    }

}

/*
 * Copyright (c) 2020 Automation Anywhere.
 * All rights reserved.
 *
 * This software is the proprietary information of Automation Anywhere.
 * You shall use it only in accordance with the terms of the license agreement
 * you entered into with Automation Anywhere.
 */
/**
 *@author: James Dickson
 */
package com.automationanywhere.botcommand.samples.commands.basic;


import com.automationanywhere.botcommand.data.Value;
import com.automationanywhere.botcommand.data.impl.RecordValue;
import com.automationanywhere.botcommand.data.impl.StringValue;
import com.automationanywhere.botcommand.data.model.Schema;
import com.automationanywhere.botcommand.data.model.record.Record;
import com.automationanywhere.botcommand.exception.BotCommandException;
import com.automationanywhere.commandsdk.annotations.*;
import com.automationanywhere.commandsdk.annotations.rules.NotEmpty;
import com.automationanywhere.commandsdk.model.AttributeType;

import com.automationanywhere.core.security.SecureString;
import com.slack.api.bolt.socket_mode.SocketModeApp;
import com.slack.api.bolt.App;
import com.slack.api.bolt.AppConfig;
import com.slack.api.model.event.AppMentionEvent;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import static com.automationanywhere.commandsdk.model.AttributeType.CREDENTIAL;
import static com.automationanywhere.commandsdk.model.DataType.RECORD;

@BotCommand(commandType = BotCommand.CommandType.Trigger)
@CommandPkg(label = "App Mention Trigger", description = "Real Time Mention Trigger", icon = "SLACK.svg", name = "appMentionTrigger",
        return_type = RECORD, return_name = "TriggerData", return_description = "Available keys: triggerType, action")
public class messageTrigger {

    private static final Map<String, SocketModeApp> taskMap = new ConcurrentHashMap<>();
    @TriggerId
    private String triggerUid;
    @TriggerConsumer
    private Consumer consumer;

    //This method is called by MessageListenerContainer when a message arrives.
    // We will enable the trigger at this point
    //@Override
    public void onMention(String action) {
        consumer.accept(getRecordValue(action));
    }

    private RecordValue getRecordValue(String action) {
        List<Schema> schemas = new LinkedList<>();
        List<Value> values = new LinkedList<>();
        schemas.add(new Schema("triggerType"));
        values.add(new StringValue("AppMentionEvent"));
        schemas.add(new Schema("action"));
        values.add(new StringValue(action));

        RecordValue recordValue = new RecordValue();
        recordValue.set(new Record(schemas, values));
        return recordValue;
    }

    @StartListen
    public void startTrigger(@Idx(index = "1", type = CREDENTIAL)
                             @Pkg(label = "App Token")
                             @NotEmpty SecureString appToken, @Idx(index = "2", type = CREDENTIAL)
                             @Pkg(label = "Bot Token")
                             @NotEmpty SecureString botToken) throws Exception {
        AppConfig appConfig = AppConfig.builder().singleTeamBotToken(botToken.getInsecureString()).build();
        App app = new App(appConfig);
        SocketModeApp socketModeApp = new SocketModeApp(appToken.getInsecureString(), app);
        app.event(AppMentionEvent.class, (req, ctx) -> {
            String incomingMsg = req.getEvent().getText();
            String responseMsg = "";
            String actionRequest = "";
            if (incomingMsg.contains("run")) {
                responseMsg = "Not a problem. Deploying the bot now!";
                actionRequest = "run";
            }
            ctx.say(responseMsg);
            //System.out.println("it worked!!!");
            onMention(actionRequest);
            return ctx.ack();
        });
        if (taskMap.get(triggerUid) == null) {
            synchronized (this) {
                if (taskMap.get(triggerUid) == null) {
                    taskMap.put(triggerUid, socketModeApp);
                    socketModeApp.start();
                }
            }
        }
    }
    /*
     * Cancel all the task and clear the map.
     */
    @StopAllTriggers
    public void stopAllTriggers() {
        taskMap.forEach((k, v) -> {
            try {
                v.stop();
            } catch (Exception e) {
                e.printStackTrace();
                throw new BotCommandException(e);
            }
            taskMap.remove(k);
        });
    }

    /*
     * Cancel the task and remove from map
     *
     * @param triggerUid
     */
    @StopListen
    public void stopListen(String triggerUid) throws Exception {
        taskMap.get(triggerUid).stop();
        taskMap.remove(triggerUid);
    }

    public String getTriggerUid() {
        return triggerUid;
    }

    public void setTriggerUid(String triggerUid) {
        this.triggerUid = triggerUid;
    }

    public Consumer getConsumer() {
        return consumer;
    }

    public void setConsumer(Consumer consumer) {
        this.consumer = consumer;
    }

}

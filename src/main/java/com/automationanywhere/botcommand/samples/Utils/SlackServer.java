package com.automationanywhere.botcommand.samples.Utils;

import com.slack.api.Slack;

public class SlackServer {


    public String getToken() {
        return Token;
    }
    public Slack getInstance() { return slack; }

    String Token;
    Slack slack;



    public SlackServer(String Token, Slack instance){
        this.Token = Token;
        this.slack = instance;
    }
}
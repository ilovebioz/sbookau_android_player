package com.sectic.sbookau.model;

/**
 * Created by bioz on 6/22/2017.
 */

public class Message {
    public String action;
    public String title;
    public String content;
    public String extraInfo;

    public Message(String sAction){
        this.action = sAction;
        switch(sAction){
            case "SUGGEST":
                this.title = "Ask For a Book";
                this.content = "Book: ";
                this.extraInfo = "Email to response: ";
                break;
            case "REPORT":
                this.title = "Report an Audio";
                this.content = "ID: ";
                this.extraInfo = "Name: ";
                break;
        }
    }

    public Message(String action, String title, String content, String extraInfo){
        this.action = action;
        this.title = title;
        this.content = content;
        this.extraInfo = extraInfo;
    }
}

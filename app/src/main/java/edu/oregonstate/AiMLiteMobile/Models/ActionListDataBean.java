package edu.oregonstate.AiMLiteMobile.Models;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by jordan_n on 7/28/2015.
 */
public class ActionListDataBean implements Serializable {

    private ArrayList<Action> actions;

    public ActionListDataBean(ArrayList<Action> actions){ this.actions = actions; }

    public ArrayList<Action> getActions() { return actions; }

}
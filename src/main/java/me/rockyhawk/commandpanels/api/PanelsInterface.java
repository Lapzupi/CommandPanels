package me.rockyhawk.commandpanels.api;

import me.rockyhawk.commandpanels.openpanelsmanager.PanelPosition;

public class PanelsInterface {

    public final String playerName;
    private Panel top;
    private Panel middle;
    private Panel bottom = null;

    public PanelsInterface(String player) {
        playerName = player;
    }

    //if all panels are closed
    public boolean allClosed() {
        return top == null && middle == null && bottom == null;
    }

    //get the panels based on position
    public void setPanel(Panel panel, PanelPosition position) {
        switch (position) {
            case TOP -> {
                if (panel == null && top != null) {
                    top.isOpen = false;
                }
                top = panel;
            }
            case MIDDLE -> {
                if (panel == null && middle != null) {
                    middle.isOpen = false;
                }
                middle = panel;
            }
            case BOTTOM -> {
                if (panel == null && bottom != null) {
                    bottom.isOpen = false;
                }
                bottom = panel;
            }
        }
    }

    //get the panels based on position
    public Panel getPanel(PanelPosition position) {
        switch (position) {
            case TOP -> {
                return top;
            }
            case MIDDLE -> {
                return middle;
            }
            case BOTTOM -> {
                return bottom;
            }
        }
        return null;
    }
}

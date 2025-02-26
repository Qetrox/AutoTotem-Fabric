package net.qlient.autototem.config;

import me.shedaniel.autoconfig.annotation.Config;

@Config(name = "autototem")
public class AutototemConfigData {

    public boolean Enabled = true;
    public boolean CheckForEffects = true;

    public int DelayInMilliseconds = 0;
    public boolean AddRandomDelay = true;
    public int MaxRandomDelay =  500;

}

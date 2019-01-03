package be.tgermain.m3uepgmatcher.epg;

public class EpgEntry {

    private String channelId;
    private String channelDisplayName;
    private String iconUri;


    public String getChannelId() {
        return channelId;
    }

    public String getChannelDisplayName() {
        return channelDisplayName;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public void setChannelDisplayName(String channelDisplayName) {
        this.channelDisplayName = channelDisplayName;
    }

    @Override
    public String toString() {
        return channelId + " - " + channelDisplayName;
    }

    public void setIconUri(String iconUri) {
        this.iconUri = iconUri;
    }

    public String getIconUri() {
        return iconUri;
    }
}

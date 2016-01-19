package pl.panryba.mc.guilds;

/**
 * @author PanRyba.pl
 */
public class CanCreateGuildResult {
    private boolean result;
    private CanCreateGuildReason reason;

    protected CanCreateGuildResult(boolean result, CanCreateGuildReason reason) {
        this.result = result;
        this.reason = reason;
    }

    public static CanCreateGuildResult Yes() {
        return new CanCreateGuildResult(true, CanCreateGuildReason.ALLOWED);
    }

    public static CanCreateGuildResult No(CanCreateGuildReason reason) {
        return new CanCreateGuildResult(false, reason);
    }

    public boolean getResult() {
        return result;
    }

    public CanCreateGuildReason getReason() {
        return reason;
    }
}

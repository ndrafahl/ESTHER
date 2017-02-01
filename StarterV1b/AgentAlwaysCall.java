/*
 * ESTHER
 * The Educational Simulated Texas Hold Em Room
 */

/**
 *
 * @author schafer
 */
public class AgentAlwaysCall extends Player {

    private final int num;

    public AgentAlwaysCall(int num) {
        this.num = num;
    }

    @Override
    public String getScreenName() {
        return "Call" + this.num;
    }

    @Override
    public String getAction(TableData data) {
        String pull = data.getValidActions();
        String[] choices = pull.split(",");
        if (pull.contains("call")) {
            return "call";
        } else  {
            return "check";
        }
    }
}

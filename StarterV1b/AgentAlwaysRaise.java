/*
 * ESTHER
 * The Educational Simulated Texas Hold Em Room
 */


/**
 *
 * @author schafer
 */
public class AgentAlwaysRaise extends Player {

    private final int num;

    public AgentAlwaysRaise(int num) {
        this.num = num;
    }


    @Override
    public String getScreenName() {
        return "Raise" + this.num;
    }

    @Override
    public String getAction(TableData data) {
        String pull = data.getValidActions();
        String[] choices = pull.split(",");
        if (pull.contains("bet")) {
            return "bet";
        } else if (pull.contains("raise")) {
            return "raise";
        } else {
            return "call";
        } 
    }
}

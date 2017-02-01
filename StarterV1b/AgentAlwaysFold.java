/*
 * ESTHER
 * The Educational Simulated Texas Hold Em Room
 */

/**
 *
 * @author schafer
 */
public class AgentAlwaysFold extends Player {

    private final int num;

    public AgentAlwaysFold(int num) {
        this.num = num;
    }

    @Override
    public String getScreenName() {
        return "Fold" + this.num;
    }

    @Override
    public String getAction(TableData data) {
        return "fold";
    }
}

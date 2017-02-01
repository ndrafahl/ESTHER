/*
 * ESTHER
 * The Educational Simulated Texas Hold Em Room
 */


import java.util.Random;

/**
 *
 * @author schafer
 */
public class AgentRandomPlayer extends Player {
    private final int num;

    public AgentRandomPlayer(int num) {
        this.num=num;
    }

    
    @Override
    public String getScreenName() {
        return "rand"+this.num;
    }



    @Override
    public String getAction(TableData data) {
        String pull = data.getValidActions();
        String[] choices = pull.split(",");
        Random randomGenerator = new Random();
        int index = randomGenerator.nextInt(choices.length);
        return choices[index];
    }
    
    
}

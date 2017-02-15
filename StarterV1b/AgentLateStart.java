import java.util.Random;

/**
 * Created by Nick Drafahl on 2/13/2017.
 */
public class AgentLateStart extends Player {
    private final int num;
    private final int[] limits = {1, 1, 1, 2, 2};

    public AgentLateStart (int num) {
        this.num = num;
    }


    @Override
    public String getScreenName() {
        return "Raise" + this.num;
    }

    @Override
    public String getAction(TableData data) {

        System.out.println("LateStart's pocket is :");
        System.out.print(EstherTools.intCardToStringCard(data.getPocket()[0]) + " ");
        System.out.println(EstherTools.intCardToStringCard(data.getPocket()[1]));

        System.out.println("Starting a new game...");

        Dealer dealer = new Dealer(data.getPlayerCount());

        Player[] simPlayers = data.getPlayers();
        simPlayers[this.num] = new AgentRandomPlayer(this.num);
        //GameManager g = new GameManager(simPlayers, dealer, false);
        GameManager g = new GameManager(simPlayers, dealer, false, limits, 3, 1 * simPlayers.length);
        int[] end = g.playGame();
        System.out.println("Final Totals");
        for (int x = 0; x < end.length; x++) {
            System.out.println((x + 1) + " "
                    + data.getPlayers()[x].getScreenName() + " had " + end[x]);
        }

        System.out.println("Finished \"new game\"");

        String pull = data.getValidActions();
        String[] choices = pull.split(",");
        Random randomGenerator = new Random();
        int index = randomGenerator.nextInt(choices.length);
        return choices[index];
    }
}

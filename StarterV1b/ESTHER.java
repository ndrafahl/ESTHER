/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import DAOFiles.NeuralNetworkDAO;
import NeuralNetwork.NeuralNetworkBluePrint;

import java.io.IOException;
import java.util.HashMap;

/**
 *
 * @author schafer
 */
public class ESTHER {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        //this can be used to easily switch between multiple play modes
        //1 = one GAME (60 hands), same cards every time the GAME is played
        //2 = one GAME, different cards every time the GAME is played
        //3 = "tournament" where there are N GAMES played,
        //         after each GAME the players shift one seat and the GAME
        //         is repeated (with the same hands from the previous GAME)

        int mode = 2;

        // this controls whether or not the MCTS Bot should read and write it's root node to a JSON file
        boolean MCTS_SERIALIZE = false;

        int[] limits = {1,1,1,2,2};
        Player[] players = new Player[6];


        //Adjust the right side of these assignments to select new agents
        players[0] = new AgentRandomPlayer(0);
        players[1] = new AgentAlwaysCall(1);
        players[2] = new AgentMCTSBot(2, MCTS_SERIALIZE);
        players[3] = new AgentAlwaysRaise(3);
        players[4] = new AgentRandomPlayer(4);
        players[5] = new NeuralNetworkPlayer("6b","test6.best");
        //players[5] = new AgentHumanCommandLine();
        //System.out.println("You will be player #6");

        if (mode == 1) {
            Dealer dealer = new Dealer(players.length);
            GameManager g = new GameManager(players, dealer, false);
            int[] end = g.playGame();
            System.out.println("Final Totals");
            for (int x = 0; x < end.length; x++) {
                System.out.println((x + 1) + " "
                        + players[x].getScreenName() + " had " + end[x]);
            }
        }
        if (mode == 2) {
            Dealer dealer = new Dealer(players.length);
            GameManager g = new GameManager(players, dealer, true);
            double startTime = System.currentTimeMillis();

            int[] end = g.playGame();

            System.out.println("Final Totals");
            for (int x = 0; x < end.length; x++) {
                System.out.println((x + 1) + " "
                        + players[x].getScreenName() + " had " + end[x]);
            }

            if (MCTS_SERIALIZE) {
                AgentMCTSBot MCTSBot = (AgentMCTSBot) players[2];
                MCTSBot.writeTree(); //writes the current tree to file: treenode.ser
            }

            double endTime = System.currentTimeMillis();
            double runTime = (endTime - startTime) / 1000;
            System.out.println("Total runtime is: " + runTime + " seconds.");
        }
        if (mode == 3) {
            //Setup HashMap to store overall results
            HashMap<String, Integer> outcome = new HashMap<>();
            for (Player player : players) {
                outcome.put(player.getScreenName(), 0);
            }


            for (int x = 0; x < players.length; x++) {
                System.out.println("Starting RND " + (x + 1) + " of the tournament.");
                Dealer dealer = new Dealer(players.length, 123456789);
                GameManager g = new GameManager(players, dealer, false);
                int[] end = g.playGame();
                System.out.println("Round Totals");
                for (int y = 0; y < end.length; y++) {
                    String name = players[y].getScreenName();
                    System.out.println((y + 1) + " "
                            + name + " had " + end[y]);
                    outcome.put(name, outcome.get(name) + end[y]);
                }
                System.out.println();

                Player[] temp = new Player[players.length];
                temp[0] = players[players.length - 1];
                for (int y = 1; y < players.length; y++) {
                    temp[y] = players[y - 1];
                }
                players = temp;
            }

            System.out.println("OVERALL OUTCOME");
            for (Player player : players) {
                String name = player.getScreenName();
                System.out.println(name + " " + outcome.get(name));
            }
        }

        if (mode == 4) {
            Dealer dealer = new Dealer(players.length);
            GameManager g = new GameManager(players, dealer, false, limits, 3, 10000);
            int[] end = g.playGame();
            System.out.println("Final Totals");
            for (int x = 0; x < end.length; x++) {
                System.out.println((x + 1) + " "
                        + players[x].getScreenName() + " had " + end[x]);
            }

        }

        if (mode == 5) {
            try {
                PreFlopDecision.buildPocketArray(5);
            } catch (IOException e) {
                throw e;
            }
        }

        if(mode == 6){
            PreflopTrainer.trainPreflop();
        }

        if(mode == 7){
            GATrainingFunc trainer = new GATrainingFunc();
            trainer.GAtrainingFunc(1000,36,4,4,4,4,
                    8,8,2,2,300,"test1.");
        }

        if(mode == 8){
            NeuralNetworkDAO fileDAO = new NeuralNetworkDAO();
            NeuralNetworkBluePrint bluePrint = fileDAO.loadNeuralNetworkList("test6.best");
            System.out.println(String.valueOf(bluePrint.getNumOfInputs()));
        }

    }

}

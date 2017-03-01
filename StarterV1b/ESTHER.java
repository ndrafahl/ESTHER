/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.HashMap;

/**
 *
 * @author schafer
 */
public class ESTHER {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        //this can be used to easily switch between multiple play modes
        //1 = one GAME (60 hands), same cards every time the GAME is played
        //2 = one GAME, different cards every time the GAME is played
        //3 = "tournament" where there are N GAMES played,
        //         after each GAME the players shift one seat and the GAME
        //         is repeated (with the same hands from the previous GAME)
        int mode = 2;

        Player[] players = new Player[6];


        //Adjust the right side of these assignments to select new agents
        players[0] = new AgentRandomPlayer(1);
        players[1] = new AgentAlwaysCall(1);
        //players[2] = new AgentRandomPlayer(2);
        players[2] = new AgentLateStart(2);
        players[3] = new AgentAlwaysRaise(1);
        players[4] = new AgentRandomPlayer(3);
        players[5] = new AgentRandomPlayer(3);
        //players[5] = new AgentHumanCommandLine();
        //System.out.println("You will be player #6");
         
        if (mode == 1) {
            Dealer dealer = new Dealer(players.length, 123456789);
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

            double endTime = System.currentTimeMillis();
            double runTime = (endTime - startTime) / 1000;
            System.out.println("Total runtime is: " + runTime + " seconds.");
        }
        if (mode == 3) {
            //Setup HashMap to store overall results
            HashMap<String,Integer> outcome = new HashMap<>();
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
                    outcome.put(name,outcome.get(name)+end[y]);
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
                System.out.println(name+" "+outcome.get(name));
            }
        }

    }

}

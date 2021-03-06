package com.heatonresearch.aifh.examples.selection;

import com.heatonresearch.aifh.evolutionary.genome.BasicGenome;
import com.heatonresearch.aifh.evolutionary.genome.Genome;
import com.heatonresearch.aifh.evolutionary.opp.selection.TournamentSelection;
import com.heatonresearch.aifh.evolutionary.population.BasicPopulation;
import com.heatonresearch.aifh.evolutionary.population.Population;
import com.heatonresearch.aifh.evolutionary.species.Species;
import com.heatonresearch.aifh.evolutionary.train.EvolutionaryAlgorithm;
import com.heatonresearch.aifh.evolutionary.train.basic.BasicEA;
import com.heatonresearch.aifh.genetic.genome.IntegerArrayGenome;
import com.heatonresearch.aifh.learning.MLMethod;
import com.heatonresearch.aifh.learning.score.ScoreFunction;
import com.heatonresearch.aifh.randomize.GenerateRandom;
import com.heatonresearch.aifh.randomize.MersenneTwisterGenerateRandom;

/**
 * This example shows how the number of rounds affects the the average score of the genome selected by
 * the tournament selection operator.  A population of 1000 genomes is created with each genome having a
 * score between 0 and 999.  There is one genome for each score.  Round counts are tried between one and ten.
 * The average score over 100k selections is reported.  As the number of rounds increases, so does the average
 * score selected.
 *
 * Sample output is shown here:
 *
 * Rounds: 1, Avg Score: 665
 * Rounds: 2, Avg Score: 749
 * Rounds: 3, Avg Score: 800
 * Rounds: 4, Avg Score: 833
 * Rounds: 5, Avg Score: 856
 * Rounds: 6, Avg Score: 874
 * Rounds: 7, Avg Score: 888
 * Rounds: 8, Avg Score: 899
 * Rounds: 9, Avg Score: 908
 * Rounds: 10, Avg Score: 915
 */
public class TournamentCompareExample {
    public static void main(String[] args) {

        // Create a new population.
        Population pop = new BasicPopulation();
        Species species = pop.createSpecies();

        // Create 1000 genomes, assign the score to be the index number.
        for(int i=0;i<1000;i++) {
            Genome genome = new IntegerArrayGenome(1);
            genome.setScore(i);
            genome.setAdjustedScore(i);
            pop.getSpecies().get(0).add(genome);
        }

        GenerateRandom rnd = new MersenneTwisterGenerateRandom();

        // Create a trainer with a very simple score function.  We do not care
        // about the calculation of the score, as they will never be calculated.
        // We only care that we are maximizing.
        EvolutionaryAlgorithm train = new BasicEA(pop,new ScoreFunction() {
            @Override
            public double calculateScore(MLMethod method) {
                return 0;
            }

            @Override
            public boolean shouldMinimize() {
                return false;
            }
        });

        // Perform the test for round counts between 1 and 10.
        for(int roundCount=1;roundCount<=10;roundCount++) {
            TournamentSelection selection = new TournamentSelection(train,roundCount);
            int sum = 0;
            int count = 0;
            for(int i=0;i<100000;i++) {
                int genomeID = selection.performSelection(rnd,species);
                Genome genome = species.getMembers().get(genomeID);
                sum+=genome.getAdjustedScore();
                count++;
            }
            sum/=count;
            System.out.println("Rounds: " + roundCount + ", Avg Score: " + sum);
        }
    }
}

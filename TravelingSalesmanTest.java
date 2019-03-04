package opt.test;

import java.util.Arrays;
import java.util.Random;
import java.io.*;

import dist.DiscreteDependencyTree;
import dist.DiscretePermutationDistribution;
import dist.DiscreteUniformDistribution;
import dist.Distribution;

import opt.SwapNeighbor;
import opt.GenericHillClimbingProblem;
import opt.HillClimbingProblem;
import opt.NeighborFunction;
import opt.RandomizedHillClimbing;
import opt.SimulatedAnnealing;
import opt.example.*;
import opt.ga.CrossoverFunction;
import opt.ga.SwapMutation;
import opt.ga.GenericGeneticAlgorithmProblem;
import opt.ga.GeneticAlgorithmProblem;
import opt.ga.MutationFunction;
import opt.ga.StandardGeneticAlgorithm;
import opt.prob.GenericProbabilisticOptimizationProblem;
import opt.prob.MIMIC;
import opt.prob.ProbabilisticOptimizationProblem;
import shared.FixedIterationTrainer;

/**
 * 
 * @author Andrew Guillory gtg008g@mail.gatech.edu
 * @version 1.0
 */
public class TravelingSalesmanTest {
    /** The n value */
    private static final int N = 50;
    /**
     * The test main
     * @param args ignored
     */
    public static void main(String[] args) throws FileNotFoundException  {
        Random random = new Random();
        // create the random points
        double[][] points = new double[N][2];
        for (int i = 0; i < points.length; i++) {
            points[i][0] = random.nextDouble();
            points[i][1] = random.nextDouble();   
        }
        // for rhc, sa, and ga we use a permutation based encoding
        TravelingSalesmanEvaluationFunction ef = new TravelingSalesmanRouteEvaluationFunction(points);
        Distribution odd = new DiscretePermutationDistribution(N);
        NeighborFunction nf = new SwapNeighbor();
        MutationFunction mf = new SwapMutation();
        CrossoverFunction cf = new TravelingSalesmanCrossOver(ef);
        HillClimbingProblem hcp = new GenericHillClimbingProblem(ef, odd, nf);
        GeneticAlgorithmProblem gap = new GenericGeneticAlgorithmProblem(ef, odd, mf, cf);
        


        double start,end,testingTime;


        PrintWriter pw = new PrintWriter(new File("testFitnessTSP.csv"));
        StringBuilder sb = new StringBuilder();

        PrintWriter pw1 = new PrintWriter(new File("testTimeTSP.csv"));
        StringBuilder sb1 = new StringBuilder();
        double ans = 0;
        for (int i  = 1; i<=8001; i+= 1000) {            
            
            start = System.nanoTime();
            RandomizedHillClimbing rhc = new RandomizedHillClimbing(hcp);      
            FixedIterationTrainer fit = new FixedIterationTrainer(rhc, i);
            fit.train();
            end = System.nanoTime();
            testingTime = end - start;
            testingTime /= Math.pow(10,9);
            ans = ef.value(rhc.getOptimal());
            System.out.println("RHC: " + ans); 
            System.out.println("time  = "+testingTime);
            System.out.println();
            sb.append(ans);
            sb.append(',');

            sb1.append(testingTime);
            sb1.append(',');


            start = System.nanoTime();
            SimulatedAnnealing sa = new SimulatedAnnealing(1E11, .95, hcp);
            fit = new FixedIterationTrainer(sa, i);
            fit.train();
            end = System.nanoTime();
            testingTime = end - start;
            testingTime /= Math.pow(10,9);
            ans = ef.value(sa.getOptimal());
            System.out.println("SA: " + ans);
            System.out.println("time  = "+testingTime);
            System.out.println();
            sb.append(ans);
            sb.append(',');

            sb1.append(testingTime);
            sb1.append(',');

            start = System.nanoTime();
            StandardGeneticAlgorithm ga = new StandardGeneticAlgorithm(200, 150, 10, gap);
            fit = new FixedIterationTrainer(ga, i);
            fit.train();
            end = System.nanoTime();
            testingTime = end - start;
            testingTime /= Math.pow(10,9);
            ans = ef.value(ga.getOptimal());
            System.out.println("GA: " + ans);
            System.out.println("time  = "+testingTime);
            System.out.println();
            sb.append(ans);
            sb.append('\n');

            sb1.append(testingTime);
            sb1.append('\n');


            // ef = new TravelingSalesmanSortEvaluationFunction(points);
            // int[] ranges = new int[N];
            // Arrays.fill(ranges, N);
            // odd = new  DiscreteUniformDistribution(ranges);
            // Distribution df = new DiscreteDependencyTree(.1, ranges); 
            // ProbabilisticOptimizationProblem pop = new GenericProbabilisticOptimizationProblem(ef, odd, df);
            // start = System.nanoTime();
            // MIMIC mimic = new MIMIC(200, 100, pop);
            // fit = new FixedIterationTrainer(mimic, i);
            // fit.train();
            // end = System.nanoTime();
            // testingTime = end - start;
            // testingTime /= Math.pow(10,9);
            // ans = ef.value(mimic.getOptimal());
            // System.out.println("MIMIC: " + ans);
            // System.out.println("time  = "+testingTime);
            // System.out.println();
            // sb.append(ans);
            // sb.append('\n');

            // sb1.append(testingTime);
            // sb1.append('\n');

        }
        pw.write(sb.toString());
        pw.close();
        pw1.write(sb1.toString());
        pw1.close();        

        // PrintWriter pw = new PrintWriter(new File("test.csv"));
        // StringBuilder sb = new StringBuilder();


        // RandomizedHillClimbing rhc = new RandomizedHillClimbing(hcp);      
        // FixedIterationTrainer fit = new FixedIterationTrainer(rhc, 200000);
        // fit.train();
        // System.out.println(ef.value(rhc.getOptimal()));
        
        // SimulatedAnnealing sa = new SimulatedAnnealing(1E12, .95, hcp);
        // fit = new FixedIterationTrainer(sa, 200000);
        // fit.train();
        // System.out.println(ef.value(sa.getOptimal()));
        
        // StandardGeneticAlgorithm ga = new StandardGeneticAlgorithm(200, 150, 20, gap);
        // fit = new FixedIterationTrainer(ga, 1000);
        // fit.train();
        // System.out.println(ef.value(ga.getOptimal()));
        
        // // for mimic we use a sort encoding
        // ef = new TravelingSalesmanSortEvaluationFunction(points);
        // int[] ranges = new int[N];
        // Arrays.fill(ranges, N);
        // odd = new  DiscreteUniformDistribution(ranges);
        // Distribution df = new DiscreteDependencyTree(.1, ranges); 
        // ProbabilisticOptimizationProblem pop = new GenericProbabilisticOptimizationProblem(ef, odd, df);
        
        // MIMIC mimic = new MIMIC(200, 100, pop);
        // fit = new FixedIterationTrainer(mimic, 1000);
        // fit.train();
        // System.out.println(ef.value(mimic.getOptimal()));
        


        // pw.write(sb.toString());
        // pw.close();
    }
}

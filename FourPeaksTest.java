package opt.test;

import java.util.Arrays;
import java.io.*;

import dist.DiscreteDependencyTree;
import dist.DiscreteUniformDistribution;
import dist.Distribution;

import opt.DiscreteChangeOneNeighbor;
import opt.EvaluationFunction;
import opt.GenericHillClimbingProblem;
import opt.HillClimbingProblem;
import opt.NeighborFunction;
import opt.RandomizedHillClimbing;
import opt.SimulatedAnnealing;
import opt.example.*;
import opt.ga.CrossoverFunction;
import opt.ga.DiscreteChangeOneMutation;
import opt.ga.SingleCrossOver;
import opt.ga.GenericGeneticAlgorithmProblem;
import opt.ga.GeneticAlgorithmProblem;
import opt.ga.MutationFunction;
import opt.ga.StandardGeneticAlgorithm;
import opt.prob.GenericProbabilisticOptimizationProblem;
import opt.prob.MIMIC;
import opt.prob.ProbabilisticOptimizationProblem;
import shared.FixedIterationTrainer;

/**
 * Copied from ContinuousPeaksTest
 * @version 1.0
 */
public class FourPeaksTest {
    /** The n value */
    private static final int N = 200;
    /** The t value */
    private static final int T = N / 5;
    
    public static void main(String[] args) throws FileNotFoundException  {
        int[] ranges = new int[N];
        Arrays.fill(ranges, 2);
        EvaluationFunction ef = new FourPeaksEvaluationFunction(T);
        Distribution odd = new DiscreteUniformDistribution(ranges);
        NeighborFunction nf = new DiscreteChangeOneNeighbor(ranges);
        MutationFunction mf = new DiscreteChangeOneMutation(ranges);
        CrossoverFunction cf = new SingleCrossOver();
        Distribution df = new DiscreteDependencyTree(.1, ranges); 
        HillClimbingProblem hcp = new GenericHillClimbingProblem(ef, odd, nf);
        GeneticAlgorithmProblem gap = new GenericGeneticAlgorithmProblem(ef, odd, mf, cf);
        ProbabilisticOptimizationProblem pop = new GenericProbabilisticOptimizationProblem(ef, odd, df);
        
        double start,end,testingTime;


        PrintWriter pw = new PrintWriter(new File("testFitnessFP.csv"));
        StringBuilder sb = new StringBuilder();

        PrintWriter pw1 = new PrintWriter(new File("testTimeFP.csv"));
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
            StandardGeneticAlgorithm ga = new StandardGeneticAlgorithm(200, 100, 10, gap);
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
            sb.append(',');

            sb1.append(testingTime);
            sb1.append(',');


            start = System.nanoTime();
            MIMIC mimic = new MIMIC(200, 20, pop);
            fit = new FixedIterationTrainer(mimic, i);
            fit.train();
            end = System.nanoTime();
            testingTime = end - start;
            testingTime /= Math.pow(10,9);
            ans = ef.value(mimic.getOptimal());
            System.out.println("MIMIC: " + ans);
            System.out.println("time  = "+testingTime);
            System.out.println();
            sb.append(ans);
            sb.append('\n');

            sb1.append(testingTime);
            sb1.append('\n');

        }
        pw.write(sb.toString());
        pw.close();
        pw1.write(sb1.toString());
        pw1.close();
    }
}

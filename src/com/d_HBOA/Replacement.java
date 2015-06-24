package com.d_HBOA;

import java.util.Arrays;

import com.z_PEA.PARAMETERLESS;
import com.z_PEA.Individual;
import com.z_PEA.Population;


abstract class IReplacement{
	abstract void replace(Population population, Individual[] newIndividuals);
}

class RestrictedReplacement extends IReplacement{
	private int windowSize; 
	
	public RestrictedReplacement(int windowSize){this.windowSize = windowSize;}

	public void replace(Population population, Individual[] newIndividuals){
		int N = population.getN();
		for(int i = 0; i < newIndividuals.length; i++){
			Individual candidate = newIndividuals[i];
			int bestPosition = -1;
			int bestDistance = Integer.MAX_VALUE;
			for(int j = 1; j < windowSize; j++){							// Find within the window, the individual CLOSEST to the candidate.
				int picked = PARAMETERLESS.random.nextInt(N);
				Individual individual = population.getIndividual(picked); 
				int distance = individual.distance(candidate);
				if(distance < bestDistance){ 
					bestPosition = picked;
					bestDistance = distance;
				}
			}
			float candidateFit = candidate.computeFitness();
			if(candidateFit > population.getFitness(bestPosition)){
				population.setIndividual(bestPosition, candidate, candidateFit);
				double bestFit = population.getBestFit();
				if(candidateFit > bestFit){									// The new individual is also the best.
					population.setBestPos(bestPosition);					// Update information about the best individual.
					population.setBestFit(candidateFit);
				}
			}
		}
	} // END: replace()
} // END: Class RestrictedReplacement



class WorstReplacement extends IReplacement{
	
	// Use this inner class to sort the current population, in ascending order of fitness.
	// BEGIN: Inner class 'PosFit'
	class PosFit implements Comparable<WorstReplacement.PosFit>{  
		int position;
		double fitness;
		
		PosFit(int position, double fitness){
			this.position = position;
			this.fitness = fitness;
		}
		
		public void setPosition(int position){this.position = position;}
		public void setFitness(double fitness){this.fitness = fitness;}
		public int getPosition(){return this.position;}
		public double getFitness(){return this.fitness;}
		
		public int compareTo(PosFit posFit){
			if (this.fitness < posFit.fitness)
				return -1;
			if (this.fitness > posFit.fitness)
				return 1;
			return 0;
		}
		
		public String toString(){
			return "(" + position + ";" + fitness + ")";
		}
	} // END: Inner class 'PosFit'
	
	private PosFit[] sortedPopulation;  
	
	public WorstReplacement(){} 												// Default constructor
	
	public void replace(Population population, Individual[] newIndividuals){
		int sortN = population.getN();
		sortedPopulation = new PosFit[sortN];
		for(int i = 0; i < sortN; i++)
			sortedPopulation[i] = new PosFit(i, population.getFitness(i));
		Arrays.sort(sortedPopulation);											// Sort population in ascending order of fitness.
		for(int i = 0; i < newIndividuals.length; i++){
			int newPos = sortedPopulation[i].getPosition();
			double newFit = newIndividuals[i].computeFitness();
			population.setIndividual(newPos, newIndividuals[i], newFit);
			double bestFit = population.getBestFit();
			if(newFit > bestFit){												// The new individual is also the best.
				population.setBestPos(newPos);									// Update information about the best individual.
				population.setBestFit(newFit);
			}
		}
	}// END: replace()
}// END: Class WorstReplacement



class FullReplacement extends IReplacement{										// NOTE: Assumes that #Population == #New Individuals
	
	public void replace(Population population, Individual[] newIndividuals){
		double newBestFit = Double.MIN_VALUE;
		for(int i = 0; i < newIndividuals.length; i++){
			double newFit = newIndividuals[i].computeFitness();
			population.setIndividual(i, newIndividuals[i], newFit);
			if(newFit > newBestFit){											// The new individual is also the best.
				population.setBestPos(i);										// Update information about the best individual.
				newBestFit = newFit;
			}
		}
		population.setBestFit(newBestFit);
	}// END: replace()
}// END: Class FullReplacement







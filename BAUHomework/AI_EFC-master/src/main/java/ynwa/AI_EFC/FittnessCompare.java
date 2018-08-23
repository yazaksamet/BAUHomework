package ynwa.AI_EFC;

import java.util.Comparator;

public class FittnessCompare implements Comparator<Chromosome> {
	public int compare(Chromosome arg0, Chromosome arg1) {
		double fittness0 = arg0.getFitness();
		double fittness1 = arg1.getFitness();
		return fittness0 > fittness1 ? -1 : ( fittness0 < fittness1 ? 1 : 0);
	}
}

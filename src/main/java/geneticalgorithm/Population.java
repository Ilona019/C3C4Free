package geneticalgorithm;

import main.Matrix;

import java.util.Arrays;
import java.util.LinkedList;

public class Population {
    private LinkedList<Individual> population;

    public Population() {
        population = new LinkedList<>();
    }

    public void addChomosome(Individual ch) {
        population.add(ch);
    }

    public Individual getAtIndex(int index) {
        return population.get(index);
    }

    public LinkedList<Individual> getPopulation() {
        return population;
    }

    public int size() {
        return this.population.size();
    }

    public String convertRoutesToString(Matrix m) {
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < population.size(); i++) {
            str.append(i).append(")").append("Weight == ").append(population.get(i).getWeightTree()).append(";\t").append("Diameter == ").append(population.get(i).getDiameter()).append(";\t").append("Leaves == ").append(population.get(i).getCountLeaves()).append(";\t").append(Arrays.toString(population.get(i).getChromomeStructure())).append("\n");
        }
        return str.toString();
    }
}

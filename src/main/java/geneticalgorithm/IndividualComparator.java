package geneticalgorithm;

import java.util.Comparator;

/**
 * @author Arefeva Ilona
 */
public class IndividualComparator implements Comparator<Individual> {

    @Override
    public int compare(Individual ind1, Individual ind2) {
        return ind2.getWeightTree() - ind1.getWeightTree();
    }
}
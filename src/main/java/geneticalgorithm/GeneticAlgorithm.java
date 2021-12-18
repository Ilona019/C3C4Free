package geneticalgorithm;

import main.Matrix;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Arefeva Ilona
 */
public class GeneticAlgorithm {

    private Population population;
    private int[] masPair;
    private ChoiceOfParents choiceParents;//Тип оператора отбора родителей.
    private CrossingType crossingType;//Тип скрещивания.
    private MutationType mutationType;
    private SelectionType selectionType;//Тип селекции.
    private Matrix matrix;
    private int n;
    private double mutationProbability;

    public GeneticAlgorithm(int n, Population population, Matrix matrix) {
        this.matrix = matrix;
        this.choiceParents = ChoiceOfParents.PANMIXIA;
        this.crossingType = CrossingType.TWO_POINT;
        this.mutationType = MutationType.UNIFORM;
        this.selectionType = SelectionType.EXCLUSION;
        this.n = n;
        mutationProbability = 0.25;
        this.population = population;
        masPair = new int[2 * population.size()];
    }

    public GeneticAlgorithm(Matrix matrix, Population population, String choiceParents, String crossingType, String mutationType, String selectionType, int n, double mutationProbability) {
        this.matrix = matrix;

        this.population = population;
        masPair = new int[2 * population.size()];
        this.choiceParents = ChoiceOfParents.PANMIXIA;
        this.crossingType = CrossingType.SINGLE_POINT;
        this.mutationType = MutationType.UNIFORM;
        this.selectionType = SelectionType.EXCLUSION;
        this.mutationProbability = mutationProbability;
        this.n = n;
    }

    public enum SelectionType {
        EXCLUSION
    }

    //Оператор селекции, отбор в новую популяцию.
    public void selection() {
        switch (selectionType) {
            case EXCLUSION:
                IndividualComparator myIndividualComparator = new IndividualComparator();
                population.getPopulation().sort(myIndividualComparator);//сортировка возрастанию диаметра и веса дерева
                int needDelete = population.getPopulation().size() - n;//надо удалить
                int deleted = 0;// кол-во удалённых хромосом
                while (deleted != needDelete) {
                    population.getPopulation().removeLast();
                    deleted++;
                }
        }

    }

    public enum ChoiceOfParents {
        PANMIXIA
    }

    //Выбор родителей, разбиение на пары.
    public void choiceParents() {
        switch (choiceParents) {
            case PANMIXIA://Оба родителя выбираются случайно.
                for (int i = 0; i < (2 * population.size()); i++) {
                    masPair[i] = (int) (Math.random() * (population.size() - 1));
                }
                break;
        }
    }

    public enum CrossingType {
        SINGLE_POINT, TWO_POINT
    }

    //Оператор скрещивания родителей.
    public void crossing() {
        switch (crossingType) {
            case SINGLE_POINT://Если  в хромосоме есть две одинаковые вершины, отличные от начала и конца и вершин соседних с ними, то померять местами их концы.
                for (int i = 1; i < masPair.length - 1; i += 2) {
                    if (masPair[i] != masPair[i - 1]) {//если пара не образуется сама с собой
                        singlePointCrossover(population.getAtIndex(masPair[i]), population.getAtIndex(masPair[i - 1]));
                    }
                }
                break;
            case TWO_POINT:
                for (int i = 1; i < masPair.length - 1; i += 2) {
                    if (masPair[i] != masPair[i - 1]) {//если пара не образуется сама с собой
                        twoPointCrossover(population.getAtIndex(masPair[i]), population.getAtIndex(masPair[i - 1]));
                    }
                }
                break;
        }
    }

    enum MutationType {
        UNIFORM
    }

    //Оператор мутации потомков.
    public void mutation() {
        switch (mutationType) {
            case UNIFORM:
                Individual currentChromosome;
                int numberMutation = 0;
                for (int i = 0; i < population.size(); i++) {
                    while (numberMutation != 3) {
                        currentChromosome = population.getAtIndex(i);
                        currentChromosome.mutation(matrix, 1);
                        numberMutation++;
                    }
                }
        }
    }

    // После мутации пересчитываем фитнес функцию.
    public void updateFitnessFunction() {
        for (int i = 0; i < population.size(); i++) {
            Individual currentIndividual = population.getAtIndex(i);
            currentIndividual.recalculateFitnessFunction(matrix);
        }
    }

    public void singlePointCrossover(Individual parentFirst, Individual parentSecond) {
        int point;
        point = (int) (Math.random() * (parentFirst.getSizeChromosome() - 2)) + 1;

        Individual descendantChromosome1 = new Individual(parentFirst);
        Individual descendantChromosome2 = new Individual(parentSecond);

        descendantChromosome1.changeChromosome(descendantChromosome2.getChromomeStructure(), point, descendantChromosome1.getSizeChromosome());
        descendantChromosome2.changeChromosome(descendantChromosome1.getChromomeStructure(), point, descendantChromosome2.getSizeChromosome());

        population.addChomosome(descendantChromosome1);
        population.addChomosome(descendantChromosome2);
    }

    public void twoPointCrossover(Individual parentFirst, Individual parentSecond) {
        // позиции скрещивания
        int randomFirstPointCrossing = (int) (Math.random() * (parentFirst.getSizeChromosome() - 2)) + 1;
        int randomSecondPointCrossing = (int) (Math.random() * (parentFirst.getSizeChromosome() - 2)) + 1;

        int randomLeftPointCrossing = Math.min(randomFirstPointCrossing, randomSecondPointCrossing);
        int randomRightPointCrossing = Math.max(randomFirstPointCrossing, randomSecondPointCrossing);

        Individual descendantChromosome1 = new Individual(parentFirst);
        Individual descendantChromosome2 = new Individual(parentSecond);

        descendantChromosome1.changeChromosome(descendantChromosome2.getChromomeStructure(), randomLeftPointCrossing, randomRightPointCrossing);
        descendantChromosome2.changeChromosome(descendantChromosome1.getChromomeStructure(), randomLeftPointCrossing, randomRightPointCrossing);

        if (!existInPopulation(descendantChromosome1))
            population.addChomosome(descendantChromosome1);
        if (!existInPopulation(descendantChromosome2))
            population.addChomosome(descendantChromosome2);

    }

    //Существует ли в population такой индивид ind?
    public boolean existInPopulation(Individual ind) {
        for (int i = 0; i < population.size(); i++) {
            if (population.getAtIndex(i).equalsChromosome(ind)) {
                return true;
            }
        }
        return false;
    }

}

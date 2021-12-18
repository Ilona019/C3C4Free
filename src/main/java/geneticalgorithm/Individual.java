package geneticalgorithm;

import java.util.*;

import main.Matrix;
import randomtree.RandomTree;

/**
 * @author Arefeva Ilona
 */
public class Individual {

    private int[] chromosome;// код Прюфера
    private int weightTree;
    private int diameter;
    private int countLeaves;

    public Individual(Matrix matrix, int countRandomCountLeaves) {
        int[] codePrufer = new int[matrix.getCountVerteces() - 2];
        Random randomArr = new Random();
        // Код Прюфера, где в основном все биты различны, с вер-ю < 0.03 могут возникнуть бит повторится
        // Хромосома с небольшим чилом листьев
        Integer[] chromosomeFewLeaves = new Integer[matrix.getCountVerteces()];
        for (int i = 0; i < chromosomeFewLeaves.length; i++) {
            double recurrenceProbability = Math.random();
            if (recurrenceProbability <= 0.03) {
                chromosomeFewLeaves[i] = randomArr.nextInt((matrix.getCountVerteces() - countRandomCountLeaves));
            } else {
                chromosomeFewLeaves[i] = i;
            }
        }
        Collections.shuffle(Arrays.asList(chromosomeFewLeaves));
        double probability = Math.random();
        // Формирую код Прюфера либо с небольшим числом листьев, либо с произвольным.
        if (probability <= 0.5) {
            for (int j = 0; j < codePrufer.length; j++) {
                codePrufer[j] = chromosomeFewLeaves[j];
            }
        } else {
            Random random = new Random();
            for (int j = 0; j < codePrufer.length; j++) {
                // nextInt(n) - случ. зн. в диапазоне [0, n)
                codePrufer[j] = random.nextInt((matrix.getCountVerteces() - countRandomCountLeaves));
            }
        }
        List<Integer>[] tree = RandomTree.pruferCode2Tree(codePrufer);
        int[] tupleMaxDistanceFromStartVertex = bfs(tree, 0, matrix);
        int[] maximumDistanceBetweenTwoVertices = bfs(tree, tupleMaxDistanceFromStartVertex[0], matrix);

        this.chromosome = codePrufer;
        this.weightTree = maximumDistanceBetweenTwoVertices[2];
        this.diameter = maximumDistanceBetweenTwoVertices[1];
        this.countLeaves = maximumDistanceBetweenTwoVertices[3];
    }

    //Конструктор для создания копии объекта
    public Individual(Individual indCopy) {
        this.chromosome = Arrays.copyOf(indCopy.getChromomeStructure(), indCopy.getSizeChromosome());
        this.weightTree = indCopy.getWeightTree();
        this.diameter = indCopy.getDiameter();
        this.countLeaves = indCopy.getCountLeaves();
    }

    // Обход дерева в ширину, получаем хэш расстояний от вершины start до всех остальных
    // и возвращаю соотвественно: tupleMaxDistance[0] - номер вершины с максимальным расстоянием до стартовой
    // tupleMaxDistance[1] - значение расстояния в ребрах
    // tupleMaxDistance[2] - вес остовного дерева
    // tupleMaxDistance[3] - число листьев
    public int[] bfs(List<Integer>[] tree, int startVertex, Matrix matrix) {
        HashMap<Integer, Integer> distance = new HashMap<>();
        Queue<Integer> queue = new LinkedList<>();
        int farthestVertex = startVertex;// номер дальней вершины
        int weightTree = 0;
        queue.add(startVertex); // Добавить start в качестве начальной вершины в очередь
        distance.put(startVertex, 0);
        int countLeaves = 0;
        // начинаем с листа
        if (tree[startVertex].size() == 1) {
            countLeaves++;
        }
        while (!queue.isEmpty()) {
            int top = queue.poll(); // Вынимаем из очереди первый узел
            int childNodeDistance = distance.get(top) + 1; // Расстояние вокруг узлов, которые еще не были посещены
            for (Integer childNodes : tree[top]) {
                if (!distance.containsKey(childNodes)) // Посещали ли ранее эту вершину? Нет в distance => не посещали
                {
                    distance.put(childNodes, childNodeDistance);
                    weightTree += matrix.getWeight(top, childNodes);
                    queue.add(childNodes);
                    farthestVertex = childNodes;
                } else if (tree[top].size() == 1) {
                    countLeaves++;
                }
            }
        }
        int[] tupleMaxDistance = new int[4];
        tupleMaxDistance[0] = farthestVertex;
        tupleMaxDistance[1] = distance.get(farthestVertex);
        tupleMaxDistance[2] = weightTree;
        tupleMaxDistance[3] = countLeaves;
        return tupleMaxDistance;
    }

    public Object[] bfsForAdditionalEdges(List<Integer>[] tree, List<Integer>[] newTree, int startVertex) {
        HashMap<Integer, Integer> distance = new HashMap<>();
        HashMap<Integer, Integer> cyclicEdges = new HashMap<>();
        Queue<Integer> queue = new LinkedList<>();
        queue.add(startVertex); // Добавить start в качестве начальной вершины в очередь
        distance.put(startVertex, 0);

        while (!queue.isEmpty()) {
            int top = queue.poll(); // Вынимаем из очереди первый узел
            int childNodeDistance = distance.get(top) + 1; // Расстояние вокруг узлов, которые еще не были посещены
            for (Integer childNodes : tree[top]) {
                if (!distance.containsKey(childNodes)) // Посещали ли ранее эту вершину? Нет в distance => не посещали
                {
                    distance.put(childNodes, childNodeDistance);
                    queue.add(childNodes);
                    if(distance.get(childNodes) > 3) {
                        childNodeDistance = 1;
                        distance.put(childNodes, childNodeDistance);
                        cyclicEdges.put(childNodes, startVertex);
                        newTree[startVertex].add(childNodes);
                        newTree[childNodes].add(startVertex);
                        childNodeDistance = childNodeDistance + 2;
                    }
                }
            }
        }
        Object[] additionalTree = new Object[2];
        additionalTree[0] = cyclicEdges;
        additionalTree[1] = newTree;
        return additionalTree;
    }

    public void recalculateFitnessFunction(Matrix matrix) {
        int[] codePrufer = this.getChromomeStructure();
        List<Integer>[] tree = RandomTree.pruferCode2Tree(codePrufer);
        int[] tupleMaxDistanceFromStartVertex = bfs(tree, 0, matrix);
        int[] maximumDistanceBetweenTwoVertices = bfs(tree, tupleMaxDistanceFromStartVertex[0], matrix);

        this.weightTree = maximumDistanceBetweenTwoVertices[2];
        this.diameter = maximumDistanceBetweenTwoVertices[1];
        this.countLeaves = maximumDistanceBetweenTwoVertices[3];
    }

    // Заменить фрагмент хромосомы начиная с индекса indexBegin до indexEnd.
    public void changeChromosome(int[] otherChromosome, int indexBegin, int indexEnd) {
        if (indexEnd + 1 - indexBegin >= 0)
            System.arraycopy(otherChromosome, indexBegin, chromosome, indexBegin, indexEnd + 1 - indexBegin);
    }

    // Хромосомы совпадают?
    public boolean equalsChromosome(Individual ind2) {
        String str1 = Arrays.toString(this.getChromomeStructure());
        String str2 = Arrays.toString(ind2.getChromomeStructure());
        return str1.equals(str2);
    }

    // Мутация в гене, заменить на случайную вершину с вероятностью mutationProbability.
    public void mutation(Matrix matrix, double mutationProbability) {
        int randomVertex;
        int positionChromosome;
        double random = Math.random();
        if (random <= mutationProbability) {
            positionChromosome = (int) (Math.random() * ((matrix.getCountVerteces() - 3) + 1));
            //a + (int)(Math.random()*((b - a) + 1)) на [a, b]
            randomVertex = (int) (Math.random() * ((matrix.getCountVerteces() - 1) + 1));
            chromosome[positionChromosome] = randomVertex;
        }
    }

    public int[] getChromomeStructure() {
        return chromosome;
    }

    public int getSizeChromosome() {
        return chromosome.length;
    }

    public int getWeightTree() {
        return this.weightTree;
    }

    public int getDiameter() {
        return this.diameter;
    }

    public int getCountLeaves() {
        return this.countLeaves;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        for (Integer ch : chromosome) {
            str.append(ch).append(" ");
        }
        return str.toString();
    }

}

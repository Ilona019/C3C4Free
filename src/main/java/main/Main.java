package main;

import geneticalgorithm.GeneticAlgorithm;
import geneticalgorithm.Individual;
import geneticalgorithm.Population;
import randomtree.RandomTree;

import java.util.*;
import java.io.FileWriter;
import java.io.IOException;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


/**
 * GA
 *
 * @author Afereva Ilona
 * @version 1.0
 */

public class Main {

    public static void main(String[] args) {

        ParserDimacs parser = new ParserDimacs();
        parser.parse("files/Taxicab_64.txt");

        ArrayList<String> listCoordinatesVertex = parser.coordinatesVertex();
        int countVertex = getCountVertex(parser.countVertex);
        Matrix matrix = new Matrix(countVertex, listCoordinatesVertex);
        matrix.printMatrix();

        int numberGeneration = 40; // кол-во поколений
        int numberPopulation = 10; // размер популяции
        Population population = new Population();

        // Значения листьев у индивидума буду брать из массива rangeLeaves, числа в диапазоне [0,  matrix.getCountVerteces() - 1 ]
        List<Integer> rangeLeaves = IntStream.range(0, matrix.getCountVerteces()).boxed()
                .collect(Collectors.toCollection(ArrayList::new));
        Collections.shuffle(rangeLeaves);
        int indexRandomCountLeaves = 0;
        // формируем начальную популяцию
        for (int i = 0; i < numberPopulation; i++) {
            int countLeaves = rangeLeaves.get(indexRandomCountLeaves);
            Individual newInd = new Individual(matrix, countLeaves);
            population.addChomosome(newInd);
            indexRandomCountLeaves++;
            if (indexRandomCountLeaves == rangeLeaves.size()) {
                indexRandomCountLeaves = 0;
            }
        }
        System.out.println("Начальная популяция индивидуумов");
        System.out.println(population.convertRoutesToString(matrix));
        int k = 0;
        while (k < numberGeneration) {
            GeneticAlgorithm geneticAlgorithm = new GeneticAlgorithm(numberPopulation, population, matrix);
            geneticAlgorithm.choiceParents();
            geneticAlgorithm.crossing();
            geneticAlgorithm.mutation();
            geneticAlgorithm.updateFitnessFunction();
            geneticAlgorithm.selection();
            k++;
        }
        System.out.println("Последняя популяция индивидуумов");
        System.out.println(population.convertRoutesToString(matrix));
        Individual bestIndivual = population.getAtIndex(0);
        HashMap<Integer, Integer> edges = getEdgesDMST(bestIndivual.getChromomeStructure());

        // Добавление ребер для образования циклов > С4
        List<Integer>[] tree = RandomTree.pruferCode2Tree(bestIndivual.getChromomeStructure());
        List<Integer>[] treeCopy = RandomTree.pruferCode2Tree(bestIndivual.getChromomeStructure());
        int weightAdditionalEdges = 0;
        Object[] additionalEdges = bestIndivual.bfsForAdditionalEdges(tree, treeCopy, 0);
        HashMap<Integer, Integer>  cyclicEdges = (HashMap<Integer, Integer>) additionalEdges[0];
        for (Map.Entry<Integer, Integer> entry : cyclicEdges.entrySet()) {
            edges.put(entry.getKey(), entry.getValue());
            weightAdditionalEdges += matrix.getWeight(entry.getValue(), entry.getKey());
        }
        int countAdditionalEdges = cyclicEdges.size();

        for (int i=0; i < matrix.getCountVerteces(); i=i+10) {
            Object[] additionalEdges2 = bestIndivual.bfsForAdditionalEdges(tree, (List<Integer>[]) additionalEdges[1], i);
            HashMap<Integer, Integer>  cyclicEdges2 = (HashMap<Integer, Integer>) additionalEdges2[0];
            for (Map.Entry<Integer, Integer> entry : cyclicEdges2.entrySet()) {
                edges.put(entry.getKey(), entry.getValue());
                weightAdditionalEdges += matrix.getWeight(entry.getValue(), entry.getKey());
            }
            countAdditionalEdges += cyclicEdges2.size();
        }

        int weightSubgraph = bestIndivual.getWeightTree() + weightAdditionalEdges;
        int countEdgesSubgraph = edges.size() + countAdditionalEdges;
        System.out.println("weightSubgraph "+weightSubgraph);
        int weightGraph = 0;
        try (FileWriter writer = new FileWriter("Arefeva_64_2.txt", false)) {
            // запись строки в файл
            String text = "c Вес подграфа = " + weightSubgraph;
            writer.write(text);
            // запись символа в файл
            writer.append('\n');
            String countVertexandEdges = "p edge " + matrix.getCountVerteces() + " " + countEdgesSubgraph;
            writer.write(countVertexandEdges);
            writer.append('\n');
            for (Map.Entry<Integer, Integer> entry : edges.entrySet()) {
                weightGraph += matrix.getWeight(entry.getValue(), entry.getKey());
                writer.write("e " + (entry.getValue() + 1) + " " + (entry.getKey() + 1));
                writer.append('\n');
            }
            System.out.println("old weight graph = "+weightGraph);
            writer.flush();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    static HashMap<Integer, Integer> getEdgesDMST(int[] code) {
        HashMap<Integer, Integer> edges = new HashMap<>();
        ArrayList<Integer> vertices = new ArrayList<>();
        List<Integer> codePrufer = Arrays.stream(code)
                .boxed()
                .collect(Collectors.toList());
        // заполняю массив всех вершин по возрастанию номеров
        for (int i = 0; i < code.length + 2; i++) {
            vertices.add(i);
        }

        while (!codePrufer.isEmpty()) {
            int curCode = codePrufer.get(0);
            for (int j = 0; j < vertices.size(); j++) {
                if (!isExistVertexInCode((ArrayList<Integer>) codePrufer, vertices.get(j))) {
                    edges.put(vertices.get(j), curCode);
                    vertices.remove(j);
                    codePrufer.remove(0);
                    break;
                }
            }
        }
        // две оставшиеся вершиные образуют последнее ребро
        edges.put(vertices.get(0), vertices.get(1));
        edges = edges.entrySet().stream()
                .sorted((k1, k2) -> -k2.getValue().compareTo(k1.getValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));
        return edges;
    }

    // ищу в коде Прюфера вершину requiredVertex
    static boolean isExistVertexInCode(ArrayList<Integer> list, int requiredVertex) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i) == requiredVertex) {
                return true;
            }
        }
        return false;
    }

    private static int getCountVertex(String countVers) {
        return Integer.parseInt(countVers.split("\\s+")[2]);
    }

}
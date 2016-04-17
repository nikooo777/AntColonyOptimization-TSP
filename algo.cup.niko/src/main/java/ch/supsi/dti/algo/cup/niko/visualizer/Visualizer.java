/*
 * http://www.oracle.com/technetwork/articles/java/architect-streams-pt2-2227132
 * .html
 * http://stackoverflow.com/questions/5533191/java-random-always-returns-the-
 * same-number-when-i-set-the-seed
 * http://stackoverflow.com/questions/12458383/java-random-numbers-using-a-seed
 */

package ch.supsi.dti.algo.cup.niko.visualizer;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import ch.supsi.dti.algo.cup.niko.Path;
import ch.supsi.dti.algo.cup.niko.TSP;
import ch.supsi.dti.algo.cup.niko.TSPParser;
import ch.supsi.dti.algo.cup.niko.solvers.NearestFirstAlgorithm;
import ch.supsi.dti.algo.cup.niko.solvers.TwoOpt;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Visualizer extends Application {

	public static void main(final String[] args) {

		launch(args);

	}

	// ALL CODE FROM NOW IS JUST FOR A GRAPHIC RAPPRESENTATION
	@Override
	public void start(final Stage primaryStage) throws Exception {
		final long overallstarttime = System.currentTimeMillis();
		final Map<String, TSP> problems = new HashMap<>();
		final Map<String, Path> solutions = new HashMap<>();
		problems.put("ch130", TSPParser.parse("ch130.tsp"));
		problems.put("d198", TSPParser.parse("d198.tsp"));
		problems.put("eil76", TSPParser.parse("eil76.tsp"));
		problems.put("fl1577", TSPParser.parse("fl1577.tsp"));
		problems.put("kroA100", TSPParser.parse("kroA100.tsp"));
		problems.put("lin318", TSPParser.parse("lin318.tsp"));
		problems.put("pcb442", TSPParser.parse("pcb442.tsp"));
		problems.put("pr439", TSPParser.parse("pr439.tsp"));
		problems.put("rat783", TSPParser.parse("rat783.tsp"));
		problems.put("u1060", TSPParser.parse("u1060.tsp"));

		final long seed = System.currentTimeMillis();
		final Random random = new Random(seed);
		for (final String s : problems.keySet()) {

			long startTime = System.currentTimeMillis();
			final Path path = new NearestFirstAlgorithm().reduce(problems.get(s), random);
			System.out.println("[" + s + "]" + "Runtime: " + (System.currentTimeMillis() - startTime) + "ms. Distance: " + path.getDistance() + ". Performance: " + path.getPerformance() * 100 + "% validation: " + path.validate());

			startTime = System.currentTimeMillis();
			final Path improvedPath = new TwoOpt(path).reduce(problems.get(s), random);
			System.out.println("\t[" + s + "]" + "Runtime: " + (System.currentTimeMillis() - startTime) + "ms. Distance: " + improvedPath.getDistance() + ". Performance: " + improvedPath.getPerformance() * 100 + "% validation: " + improvedPath.validate());
			solutions.put(s, path);
		}
		System.out.println("Overall Runtime: " + (System.currentTimeMillis() - overallstarttime) / 1000. + "s");

		displaySolutions(primaryStage, problems, solutions);
	}

	private void displaySolutions(final Stage primaryStage, final Map<String, TSP> problems, final Map<String, Path> solutions) {
		final TabPane tabPane = new TabPane();
		for (final String s : solutions.keySet()) {
			final Tab tab = new Tab(s);
			primaryStage.setTitle(s);
			final Group root = new Group();
			final Canvas canvas = new Canvas(800, 800);
			final GraphicsContext gc = canvas.getGraphicsContext2D();
			drawTSP(gc, problems.get(s), solutions.get(s));
			root.getChildren().add(canvas);
			root.setStyle("-fx-padding: 20 20 20 20;");
			tab.setContent(root);
			tabPane.getTabs().add(tab);
		}
		primaryStage.setScene(new Scene(tabPane));
		primaryStage.show();
	}

	private void drawTSP(final GraphicsContext gc, final TSP problem, final Path solution) {

		double farthestCoord = 0.0;

		for (int i = 0; i < problem.getSize(); i++) {
			if (problem.getX(i) > farthestCoord)
				farthestCoord = problem.getX(i);
			if (problem.getY(i) > farthestCoord)
				farthestCoord = problem.getY(i);
		}

		final double zoom = 750 / farthestCoord;

		for (int i = 0; i < problem.getSize(); i++) {
			gc.setStroke(Color.BLACK);
			gc.fillOval(problem.getX(i) * zoom, problem.getY(i) * zoom, 5, 5);
			gc.setStroke(Color.RED);
			gc.strokeText(new Integer(i).toString(), (problem.getX(i) - 0.5) * zoom, (problem.getY(i) - 0.5) * zoom);
		}

		gc.setStroke(Color.BLUE);
		final int[] test = solution.getSolution();
		gc.setLineWidth(1);
		for (int i = 0; i < test.length - 1; i++) {
			gc.strokeLine((problem.getX(test[i]) + 0.4) * zoom, (problem.getY(test[i]) + 0.4) * zoom, (problem.getX(test[i + 1]) + 0.4) * zoom, (problem.getY(test[i + 1]) + 0.4) * zoom);
		}
		gc.strokeLine((problem.getX(test[test.length - 1]) + 0.4) * zoom, (problem.getY(test[test.length - 1]) + 0.4) * zoom, (problem.getX(test[0]) + 0.4) * zoom, (problem.getY(test[0]) + 0.4) * zoom);
	}
}

/*
 * http://www.oracle.com/technetwork/articles/java/architect-streams-pt2-2227132.html
 * http://stackoverflow.com/questions/5533191/java-random-always-returns-the-same-number-when-i-set-the-seed
 * http://stackoverflow.com/questions/12458383/java-random-numbers-using-a-seed
 */

package ch.supsi.dti.algo.cup.niko.visualizer;

import java.util.List;

import ch.supsi.dti.algo.cup.niko.TSP;
import ch.supsi.dti.algo.cup.niko.solvers.TwoOpt;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Visualizer extends Application
{
	private static String toursHomePath = "tours/";
	private static String[] allTours = { "eil76.tsp", "kroA100.tsp", "ch130.tsp", "d198.tsp", "lin318.tsp", "pcb442.tsp", "pr439.tsp", "rat783.tsp", "u1060.tsp", "fl1577.tsp" };
	private static TSP problem = null;

	public static void main(String[] args)
	{

		problem = FileParser.readData(toursHomePath + allTours[2]);
		problem.computeDistances();

		NearestNeighbour.computeAlgorithm(problem, 0);
		// DoubleNearestNeighbour.computeAlgorithm(problem, 0);

		TwoOpt.computeAlgorithm(problem, false);
		// TriOpt.computeAlgorithm(problem, false);

		System.out.println("---------------DONE---------------");
		System.out.println(problem.getName());
		System.out.println(problem.getMyPathLength());
		System.out.println(problem.calcError());

		/*
		 * for (int i = 0; i < problem.getMySolution().length; i++) {
		 * if(i % 10 == 0) {
		 * System.out.println();
		 * }
		 * System.out.print(problem.getMySolution()[i]+1 + " -> ");
		 * }
		 */
		// GRAPHIC RAPPRESENTATION
		launch(args);

	}

	// ALL CODE FROM NOW IS JUST FOR A GRAPHIC RAPPRESENTATION
	@Override
	public void start(Stage primaryStage) throws Exception
	{
		primaryStage.setTitle("TSP");
		Group root = new Group();
		Canvas canvas = new Canvas(800, 800);
		GraphicsContext gc = canvas.getGraphicsContext2D();
		drawTSP(gc);
		root.getChildren().add(canvas);
		primaryStage.setScene(new Scene(root));
		primaryStage.show();

	}

	private void drawTSP(GraphicsContext gc)
	{

		List<City> cities = problem.getListOfCities();
		double farthestCoord = 0.0;

		for (City c : cities)
		{
			if (c.getX() > farthestCoord)
			{
				farthestCoord = c.getX();
			} else if (c.getY() > farthestCoord)
			{
				farthestCoord = c.getY();
			}
		}

		double zoom = 750 / farthestCoord;

		for (int i = 0; i < cities.size(); i++)
		{
			gc.setStroke(Color.BLACK);
			gc.fillOval(cities.get(i).getX() * zoom, cities.get(i).getY() * zoom, 5, 5);
			gc.setStroke(Color.RED);
			gc.strokeText(new Integer(i + 1).toString(), (cities.get(i).getX() - 0.5) * zoom, (cities.get(i).getY() - 0.5) * zoom);
		}

		gc.setStroke(Color.BLUE);
		int[] test = problem.getMySolution();
		gc.setLineWidth(1);
		for (int i = 0; i < test.length - 1; i++)
		{
			gc.strokeLine((cities.get(test[i]).getX() + 0.4) * zoom, (cities.get(test[i]).getY() + 0.4) * zoom, (cities.get(test[i + 1]).getX() + 0.4) * zoom, (cities.get(test[i + 1]).getY() + 0.4) * zoom);
		}
		gc.strokeLine((cities.get(test[test.length - 1]).getX() + 0.4) * zoom, (cities.get(test[test.length - 1]).getY() + 0.4) * zoom, (cities.get(test[0]).getX() + 0.4) * zoom, (cities.get(test[0]).getY() + 0.4) * zoom);
	}

}

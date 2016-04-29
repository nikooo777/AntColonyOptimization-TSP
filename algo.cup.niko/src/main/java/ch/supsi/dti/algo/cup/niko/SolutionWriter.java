package ch.supsi.dti.algo.cup.niko;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class SolutionWriter {

	public static void storeSolution(Tour solution, TSP structure) {
		try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(structure.getName() + ".tour"))) {
			writer.write("NAME : " + structure.getName());
			writer.newLine();
			writer.write("COMMENT : Optimum tour for " + structure.getName() + " (" + solution.getTourLength() + ")");
			writer.newLine();
			writer.write("TYPE : TOUR");
			writer.newLine();
			writer.write("DIMENSION : " + structure.getSize());
			writer.newLine();
			writer.write("TOUR_SECTION");
			writer.newLine();
			int[] sol = solution.getSolution();
			for (int i = 0; i < structure.getSize(); i++) {
				writer.write(sol[i] + '0');
				writer.newLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

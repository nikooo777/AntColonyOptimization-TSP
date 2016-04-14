package ch.supsi.dti.algo.cup.niko;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Niko
 *
 */
public class TSPParser
{
	public static TSP parse(String algorithm)
	{
		String fileName = "problems/" + algorithm;
		List<String> list = new ArrayList<>();
		double[][] matrix;
		int index;
		boolean found;
		int distance = 0;
		try (Stream<String> stream = Files.lines(Paths.get(fileName)))
		{
			list = stream.collect(Collectors.toList());
			int length = list.size();
			matrix = new double[length][2];
			found = false;
			index = 0;
			String name = null;
			String comment = null;
			TSP.Type type = null;
			int dimension = -1;
			int bestKnown = -1;

			for (String line : list)
			{
				if (!found)
				{
					// metadata
					///////////////////////////////////////////////////////////
					if (line.startsWith("NAME"))
					{
						name = line.split(":")[1].trim();

					} else if (line.startsWith("COMMENT"))
					{
						comment = line.split(":")[1].trim();
					} else if (line.startsWith("TYPE"))
					{
						String sType = line.split(":")[1].trim();
						switch (sType)
						{
						case "TSP":
							type = TSP.Type.TSP;
						case "TOUR":
							type = TSP.Type.TOUR;
							break;
						default:
							type = null;
							break;
						}
					} else if (line.startsWith("DIMENSION"))
					{
						dimension = Integer.parseInt(line.split(":")[1].trim());
					} else if (line.startsWith("BEST_KNOWN"))
					{
						bestKnown = Integer.parseInt(line.split(":")[1].trim());
					}
					///////////////////////////////////////////////////////////

					found = line.equals("NODE_COORD_SECTION");
					continue;
				}
				if (line.equals("EOF"))
					break;
				String[] bundle = line.split(" ", 3);
				// System.out.println(Arrays.toString(bundle));
				matrix[index][0] = Double.parseDouble(bundle[1]);
				matrix[index][1] = Double.parseDouble(bundle[2]);
				index++;
			}

			return new TSP(name, comment, type, dimension, bestKnown, matrix);

		} catch (IOException e)
		{
			e.printStackTrace();
		}
		return null;
	}
}

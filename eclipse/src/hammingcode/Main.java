package hammingcode;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;


/*
 * Code developed for the first assignment of the Distributed System course.
 * Author: Matteo Nardini
 */

public class Main
{
	
	public static void main(String[] args) throws IOException {
		
		String input = "";
		String output = "";
		
		//input = "IPv6";
		//input = "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.";
		input = new String(Files.readAllBytes(Paths.get("divina_commedia_x12.txt")), StandardCharsets.UTF_8);
		
		System.out.println("Beginning encode...");
		long time = System.nanoTime();
		
		try {
			Hamming.encode(input, "hamming.out");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println("Beginning decode...");
		try {
			output = Hamming.decode("hamming.out");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//System.out.println("Given input: " + input);
		//System.out.println("Obtained output: " + output);
		time = System.nanoTime() - time;
		System.out.println("The input and the output are " + (input.equals(output) ? "" : "NOT ") + "EQUAL");
		System.out.println("Time required: " + time / 1000000 + "ms");

	}
	

}

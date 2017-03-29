package hammingcode;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Hamming
{
	/* The positions of the various bits inside the byte. Right-most bit is position 0.*/
	final static private int D1_POS = 6;
	final static private int D2_POS = 5;
	final static private int D3_POS = 4;
	final static private int D4_POS = 3;
	final static private int P1_POS = 2;
	final static private int P2_POS = 1;
	final static private int P3_POS = 0;
	
	
	public static void encode(String message, String filename) throws IOException
	{
		StringBuffer sb = new StringBuffer();
		
		message.chars().forEach(c -> {
			String tmp = Integer.toBinaryString(c);
			sb.append(String.format("%8s", tmp).replace(' ', '0'));
		});
		
		int currPos = 0;

		String converted = sb.toString(); // Converted contains the message represented as a string of 0s and 1s
		OutputStream out = new BufferedOutputStream(new FileOutputStream(filename, false));
		
		int curr; 
		while (currPos < converted.length())
		{
			curr = 0b10000000; //256, leftmost bit not used
		
			// Sets the d bits
			for (int i = 0; i <  4; i++)
			{
				if (converted.charAt(currPos + i) == '1')
					curr = setBit(curr, 1, 6-i);
			}
			
			// Sets the p bits
			curr = computeParityBits(curr);
			
			out.write((byte) curr);
			
			currPos += 4;
		}
		
		out.flush();
		out.close();	
		
	}
	
	public static String decode(String filename) throws IOException
	{
		InputStream in = new BufferedInputStream(new FileInputStream(filename));
		StringBuilder sb = new StringBuilder();
		
		int curr = 0;
		int comPar;
		boolean p1, p2, p3;
		int errorPos;
		
		while ((curr = in.read()) != -1)
		{
			errorPos = -1;
			comPar = computeParityBits(curr); // Computes the parity bits
			
			// Looks for where the computed parity bits do not match with the read ones
			p1 = getBit(comPar, P1_POS) != getBit(curr, P1_POS);
			p2 = getBit(comPar, P2_POS) != getBit(curr, P2_POS);
			p3 = getBit(comPar, P3_POS) != getBit(curr, P3_POS);
			
			if (p1 && p2)
				errorPos = D2_POS;
			
			if (p1 && p3)
				errorPos = D1_POS;
			
			if (p2 && p3)
				errorPos = D4_POS;
			
			if (p1 && p2 && p3)
				errorPos = D3_POS;
			
			// If an error is detected, the corresponding bit is flipped
			if (errorPos != -1) 
				curr = flipBit(curr, errorPos);
			
			// Converts this byte back to a string of zeros and ones
			for (int i = D1_POS; i >= D4_POS; i--)
			{
				sb.append(getBit(curr, i) > 0 ? 1 : 0);
			}
		}
		
		in.close();
		
		//b contains the corrected content of the file with the parity bit removed represented as a string
		//of 0s and 1s.
		String b = sb.toString(); 
		sb = new StringBuilder();
		char currChar;
		for (int i = 0; i < b.length(); i += 8)
		{
			//Every piece of eight 0s and 1s is converted to a char
			currChar = Character.toChars(Integer.parseInt(b.substring(i, i + 8), 2)) [0];
			sb.append(currChar);
		}
		
		return sb.toString();
	}
	
	
	
	
	
	private static int computeParityBits(int curr)
	{
		int currParity = 0;
		
		//p1
		currParity = xorAtPos(curr, D1_POS, D2_POS, D3_POS);
		curr = setBit(curr, currParity, P1_POS);
		
		//p2
		currParity = xorAtPos(curr, D2_POS, D3_POS, D4_POS);
		curr = setBit(curr, currParity, P2_POS);
		
		//p3
		currParity = xorAtPos(curr, D3_POS, D4_POS, D1_POS);
		curr = setBit(curr, currParity, P3_POS);
		
		return curr;
	}
	
	
	
	// For the explanation of the following methods, look at README.md
	
	private static int getBit(int source, int pos)
	{
		return source & (1 << pos);
	}
	
	private static int setBit(int source, int value, int pos)
	{
		if (value > 0)
			return source | (1 << pos); // Sets bit to one
		else
			return source & ~(1 << pos); // Sets bit to zero
	}
	
	private static int flipBit(int source, int pos)
	{
		return source ^ (1 << pos);
	}
	
	private static int xorAtPos(int source, int...positions)
	{
		int ans = 0;

		for (int i = 0; i < positions.length; i++)
		{
			ans = ans | (source & (1 << positions[i]));
		}
		
		return Integer.bitCount(ans) % 2 != 0 ? 1 : 0;
	}
}

package com.dataman.gitstats;

public class CodeTest {
	
	static String LINETAG = "[line]";
	public static void main(String[] args) {
//		
//		String lines="\n \\n+\n +\n \\\n+\n+\n+\n+\n+\n+\n ";
//		System.out.println(lines);
//		
//		String r= lines.replaceAll("\n", "[?line?]");
//		System.out.println(r);
		
		String diff="--- a/src/test/java/test/Base.java\n+++ b/src/test/java/test/Base.java\n@@ -40,6 +40,12 @@ class Base {\n \\n+\n +\n \\\n+\n+\n+\n+\n+\n+\n         if (r < 0.1) {\n             fail(\"oops\");\n         } else if (r < 0.2) {\n";
		System.out.println(diff);
		System.out.println("---------------------------------------------");
//		diff = diff.replaceAll("\n", LINETAG);
//		System.out.println(diff);
		System.out.println("---------------------------------------------");
		String[] ls= diff.split("\n");
		for (String line : ls) {
			System.out.println(line);
		}
		
		
		
		
	}
}

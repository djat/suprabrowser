package ss.util;


public class Test {


	public static void main(String[] args) {
	    
	    
	    String one = "C:/Documents and Settings/david/My Documents/test/test2/";
	        
	    String two = "C:/Documents and Settings/david/My Documents";
	    //String two = "C:/Doc";
	    
	    System.out.println("INDEX "+one.replaceAll(two,""));
	    
	        
		//String body = "http://www.suprasphere.com \n\n\n\tasdfadsf http://www.atabike.com";
		
	//	Perl5Util util = new Perl5Util();
		//util.match("/http:///[0-9a-z_A-Z|\\!|\\@|\\#|\\$|\\%|\\^|\\&|\\*|\\(|\\)]/",body);
		/*util.match("/http:\\/\\/.*$/",body);
		MatchResult result = util.getMatch();
		
		if (result!=null) {
			
			System.out.println("RESULT: "+result);
		}
		else {
			System.out.println("was null");
		}
		*/
		
		/*
		int groups;
		 PatternMatcher matcher;
		 PatternCompiler compiler;
		 Pattern pattern;
		 PatternMatcherInput input;
		 String patternString = "http:\\/\\/.*";

		 compiler = new Perl5Compiler();
		 matcher  = new Perl5Matcher();

		 try {
		   pattern = compiler.compile(patternString);
		 } catch(MalformedPatternException e) {
		   System.out.println("Bad pattern.");
		   System.out.println(e.getMessage());
		   return;
		 }

		 input   = new PatternMatcherInput(body);
		 MatchResult result = null;
		 while(matcher.contains(input, pattern)) {
		   result = matcher.getMatch();  
		   // Perform whatever processing on the result you want.
		   // Here we just print out all its elements to show how its
		   // methods are used.
		 
		   System.out.println("Match: " + result.toString());
		   System.out.println("Length: " + result.length());
		   groups = result.groups();
		   System.out.println("Groups: " + groups);
		   System.out.println("Begin offset: " + result.beginOffset(0));
		   System.out.println("End offset: " + result.endOffset(0));
		   System.out.println("Saved Groups: ");

		   // Start at 1 because we just printed out group 0
		   for(int group = 1; group < groups; group++) {
			 System.out.println(group + ": " + result.group(group));
			 System.out.println("Begin: " + result.begin(group));
			 System.out.println("End: " + result.end(group));
		   }
		 }
		 */		
	}
	

}

import java.io.BufferedReader; 
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList ; 
public class SearchEngine {
	public static void main(String[] args) {
	    URL url;
	    InputStream is = null;
	    BufferedReader br , br1 ;
	    String line;
	    System.setProperty("jsse.enableSNIExtension", "false");
	    try {
	        url = new URL("https://biotech.iitm.ac.in/welcome");
	        File f1 = new File ("Urls.txt") ; 
	        File f2 = new File ("Content.txt") ; 
	        if ( !f2.exists())
	        	f2.createNewFile() ; 
	        if ( !f1.exists() )
	        	f1.createNewFile() ; 
	        FileWriter fw = new FileWriter(f1.getAbsoluteFile()) , fw2 = new FileWriter(f2.getAbsoluteFile()) ;
	        BufferedWriter bw1 = new BufferedWriter(fw)  , bw2 = new BufferedWriter(fw2); 
	        is = url.openStream();  // throws an IOException
	        br = new BufferedReader(new InputStreamReader(is));
	        //String str ; 
	        char ch ; 
	        ArrayList<Url> urls = new ArrayList<Url>() ; 
	        while ((line = br.readLine()) != null) {
	        	Content(line,bw2) ; 
	           	Urls ( line , urls) ; 	        		
	        }
	        bw2.close() ; 
	        Url u ; 
	        /*for ( int i = 0 ; i < urls.size() ; i++ )
	        {
	        	u = urls.get(i) ; 
	        	System.out.println( u.title + " " + u.link ) ; 
 	        }*/
	        br =  new BufferedReader(new FileReader("Content.txt")) ; 
	        Filtering ( br , bw1 ) ; 
	        br.close() ;
	        bw1.close() ; 
	    } catch (MalformedURLException mue) {
	         mue.printStackTrace();
	    } catch (IOException ioe) {
	         ioe.printStackTrace();
	    } finally {
	        try {
	            if (is != null) is.close();
	        } catch (IOException ioe) {
	            // nothing to see here
	        }
	    }
	}
	
public static void Urls ( String line , ArrayList<Url> al ) throws IOException 
{
	String a , b;
	boolean titleEmpty ; 
	int p , q ; 
	char ch ; 
	while ( line.contains("href"))
	{
		titleEmpty = false ; 
		Url u = new Url() ; 
		p = line.indexOf("href") ; 
		a = line.substring(p) ; 
		p = a.indexOf('"') + 1  ; 
		while ( a.charAt(p) != '"' && p < a.length() -1 )
			u.link += a.charAt(p++) ; 
		if ( a.contains("title"))
		{
			q = a.indexOf("title") ; 
			b = a.substring(q) ; 
			q = b.indexOf('"') + 1 ; 
			while ( b.charAt(q) != '"' )
				u.title += b.charAt(q++) ; 
			if ( u.title.isEmpty())
				titleEmpty = true ; 
			else
				if ( !u.link.isEmpty() )
					al.add(u) ; 
			if ( q < b.length() )
				line = b.substring(q) ; 
			else
				line = " " ; 
		}
		if ( !a.contains("title") || titleEmpty )
		{
			p = a.indexOf('>') + 1 ;
			if ( p < a.length() )
			{
				if ( a.contains("<blink>"))
				{
					p = a.indexOf("<blink>") ;
					a = a.substring(p) ; 
					p = a.indexOf('>') + 1 ; 
				}
				if ( a.contains("<b>"))
				{
					p = a.indexOf("<b>") ;
					a = a.substring(p) ; 
					p = a.indexOf('>') + 1 ; 
				}
				if ( p < a.length() )
				{
					while ( a.charAt(p) != '<' && p < a.length() -1 )
						u.title += a.charAt(p++) ; 
				}
			}
			if ( !u.link.isEmpty() && !u.title.isEmpty() )
				al.add(u) ; 
			if ( p < a.length() )
				line = a.substring(p) ; 
			else
				line = " " ; 	
		}
	}
}
	
	
private static void Content ( String line , BufferedWriter bw ) throws IOException
{
	int len = line.length() ;
	//bw.write(line+"\n") ; 
		for ( int i = 0 ; i < line.length() ; i++ )
		{
			if ( line.charAt(i) == '<')
			{
				while ( line.charAt(i) != '>'  )
				{
					i++ ;
					if ( i == len )
						break ; 
				}
					 
			}
			if ( i != len )
				if ( line.charAt(i) != '>' )
					bw.write(line.charAt(i)) ;
		}
		bw.write("\n") ;	  
}

public static boolean isHtmlTag ( String line )
{
	//ArrayList<String> tags = new ArrayList<String>() ;
	String tag[] = { "display" , "sidebar" , "#header" , "background" , "padding" , "color" , "font-weight" , 
			"text-decoration" , "font-size" , "margin" , "top" , "width" , "border", "indent" ,"float" , "list-style"
			, "align" , "cellspacing" , "table" , "type" , "<script>" , "@import" , "#contents" , "#wrapper" , 
		"font-family" , "height" , "position" , "line-height" , "visibility" ,"font-style" , "font-stretch" , 
		"font-variant" , "#news" , "#event" , "footer" , "font" , "navmenu" , "url" , "animation" ,"directionNav"
		, "controlNav" , "pauseOnHover" , "slideshow" , "pauseTime" ,"boxRows" , "boxCols" , "animSpeed" ,"reverse:"
		, "direction:" , "manualAdvance:" , "effect:","slices:" ,"#site-title"} ;
	int len = tag.length ; 
	for ( int i = 0 ; i < len ; i++ )
		if (line.contains(tag[i]))
			return true ; 
	return false ; 
}

private static void Filtering ( BufferedReader br , BufferedWriter bw ) throws IOException
{
	String line , trim ; 
    while( (line = br.readLine()) != null )
    {
    	 trim = line.trim() ; 
    	 if (!isIllegal(trim) )
    		 bw.write(line+"\n") ; 
    }
    bw.close() ;
}

private static boolean isIllegal ( String str )
{
	if ( str.contains("*") || str.contains("{") || str.contains("}") || str.isEmpty() || str.contains("http") || str.contains(";") || isHtmlTag(str))
		return true ; 
	return false ; 
}
}





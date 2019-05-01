package asr.proyectoFinal.servlets;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.annotation.JsonFormat.Features;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.NaturalLanguageUnderstanding;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.AnalysisResults;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.AnalyzeOptions;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.EntitiesOptions;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.EntitiesResult;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.KeywordsOptions;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.MetadataOptions;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.SentimentOptions;
import com.ibm.watson.developer_cloud.service.security.IamOptions;

import asr.proyectoFinal.dao.CloudantPalabraStore;
import asr.proyectoFinal.dominio.Palabra;
import asr.proyectoFinal.services.Traduttore;
import asr.proyectoFinal.services.Traduttore;
import clover.antlr.collections.List;
import javassist.compiler.ast.Keyword;


/**
 * Servlet implementation class Controller
 */
@WebServlet(urlPatterns = {"/listar", "/insertar", "/hablar", "/nlu"})
public class Controller extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		PrintWriter out = response.getWriter();
		out.println("<html><head><meta charset=\"UTF-8\"></head><body>");
		
		CloudantPalabraStore store = new CloudantPalabraStore();
		System.out.println(request.getServletPath());
		switch(request.getServletPath())
		{
			case "/listar":
				if(store.getDB() == null)
					  out.println("No hay DB");
				else
					out.println("Palabras en la BD Cloudant:<br />" + store.getAll());
				
				break;
				
			case "/insertar":
				Palabra palabra = new Palabra();
				String parametro = request.getParameter("palabra");

				if(parametro==null)
				{
					out.println("usage: /insertar?palabra=palabra_a_traducir");
				}
				else
				{
					if(store.getDB() == null) 
					{
						out.println(String.format("Palabra: %s", palabra));
					}
					else
					{
						parametro = Traduttore.translate(parametro, "es", "en", false);
						palabra.setName(parametro);
						store.persist(palabra);
					    out.println(String.format("Almacenada la palabra: %s", palabra.getName()));			    	  
					}
				}
				break;
				
			case "/nlu":			
	/*	
			IamOptions options = new IamOptions.Builder().apiKey("U7t-aoMI9ZOSercBaWE4bEHqrv6D62XRx9iAMz3UFSVA").build();
			NaturalLanguageUnderstanding naturalLanguageUnderstanding = new NaturalLanguageUnderstanding("2018-11-16", options);
			naturalLanguageUnderstanding.setEndPoint("https://gateway-lon.watsonplatform.net/natural-language-understanding/api");

			/*String text ="IBM is an American multinational technology " 
						 + "company headquartered in Armonk, New York, "
						 + "United States, with operations in over 170 countries.";
				*/
	/*		String text="Ferrari, Lamborgini. there are some cars that cost a lot. But if you buy those cars in Egypt yoy can pay less";	
		
			EntitiesOptions entitiesOptions = new EntitiesOptions.Builder()
				.emotion(true)
				.sentiment(true)
				.mentions(true)
				.limit(50)
				.build();

			KeywordsOptions keywordsOptions = new KeywordsOptions.Builder()
				 .emotion(true)
				 .sentiment(true)
			     .limit(5)
			     .build();
				
			com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.Features features=new com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.Features.Builder()  
				  .entities(entitiesOptions)
				  .keywords(keywordsOptions)
				  .build();

			AnalyzeOptions parameters = new AnalyzeOptions.Builder()
				  .text(text)
				  .features(features)
				  .build();

			AnalysisResults response1 = naturalLanguageUnderstanding
				  .analyze(parameters)
				  .execute();
				
			//System.out.println(response1);	
			//out.println(response1);
				
		///////////// JSON ///////////////////////////////////////////////////////////////////////
				
			String nluJSON = response1.toString();
			JsonParser parser = new JsonParser();
			JsonObject rootObj = parser.parse(nluJSON).getAsJsonObject();
			JsonArray nlu = rootObj.getAsJsonArray("usage:");				
			JsonArray KEY= rootObj.getAsJsonArray("keywords");
			JsonArray ent =rootObj.getAsJsonArray("entities");
						
			//arraylist di keywords
			ArrayList<String>a=new ArrayList<String>();
	    	ArrayList<String>b=new ArrayList<String>();
			ArrayList<String>c=new ArrayList<String>();
			ArrayList<String>emo=new ArrayList<String>();
			
			//arraylist di entities
			ArrayList<String>d=new ArrayList<String>();
			ArrayList<String>e=new ArrayList<String>();
			ArrayList<String>f=new ArrayList<String>();
	    	ArrayList<String>g=new ArrayList<String>();
			ArrayList<String>h=new ArrayList<String>();
				
			
				System.out.println("*********************************************************");
/// STAMPA OUTPUT
				System.out.println("Il testo analizzato è: " +text);
				System.out.println("La lingua del testo e': "+rootObj.get("language"));
				out.println("Il testo analizzato è: "+text+"<br>");
				out.println(" "+"<br>");
				out.println("La lingua del testo e': "+rootObj.get("language"));
				out.println(" "+"<br>");
				
				for(int i=0;i<KEY.size();i++) {
					 a.add(KEY.get(i).getAsJsonObject().get("text").getAsString());
					 b.add(KEY.get(i).getAsJsonObject().get("relevance").getAsString());
					 c.add(KEY.get(i).getAsJsonObject().get("sentiment").getAsJsonObject().get("score").getAsString());
					 emo.add(KEY.get(i).getAsJsonObject().get("emotion").getAsJsonObject().get("anger").getAsString());
					 emo.add(KEY.get(i).getAsJsonObject().get("emotion").getAsJsonObject().get("disgust").getAsString());
					 emo.add(KEY.get(i).getAsJsonObject().get("emotion").getAsJsonObject().get("fear").getAsString());
					 emo.add(KEY.get(i).getAsJsonObject().get("emotion").getAsJsonObject().get("joy").getAsString());
					 emo.add(KEY.get(i).getAsJsonObject().get("emotion").getAsJsonObject().get("sadness").getAsString());
					  }
				
				System.out.println();
				System.out.println("Results keywords:");
				out.println("Results keywords:"+"<br>");
		 
		 //STAMPA
				 for(int i=0;i<KEY.size();i++) {
						 System.out.println();
						 System.out.println("text -" + a.get(i)+"- ha relevance -"+b.get(i)+"-. Lo score finale è di -"+c.get(i)+"-");
						 out.println("Il text " + a.get(i)+" ha relevance " + b.get(i)+" e un punteggio di sentiment-score pari a " + c.get(i)+"<br>");
					 }
		 
				 System.out.println("emotion:");
				 out.println("emotion:"+"<br>");
				 for(int j=0;j<5;j++) {
					if(j==0) {
						 System.out.print("anger:   ");
						 out.print("anger:   ");
					 }  
					 if(j==1) {
						 System.out.print("disgust:   ");
						 out.print("disgust:   ");
					 } 
					 if(j==2) {
						 System.out.print("fear:   ");
						 out.print("fear:   ");
					 } 
					 if(j==3) {
						System.out.print("joy:   "); 
						out.print("joy:   ");
					 }
					 if(j==4) {
						 System.out.print("sadness:   ");
						 out.print("sadness:   ");
					 } 
					 System.out.println(emo.get(j));
					 out.println(" ");
					 out.println(emo.get(j)+"<br>");
				 } 
				 
				 for(int i=0;i<ent.size();i++) {
					 d.add(ent.get(i).getAsJsonObject().get("type").getAsString());
					 e.add(ent.get(i).getAsJsonObject().get("text").getAsString());
					 f.add(ent.get(i).getAsJsonObject().get("relevance").getAsString());
					 g.add(ent.get(i).getAsJsonObject().get("sentiment").getAsJsonObject().get("score").getAsString());	 
				 }
		 
				 System.out.println();
				 System.out.println("Results entities:");
				 out.println("                                   "+"<br>");
				 out.println("Results entities:"+"<br>");
				 
				 for(int i=0;i<ent.size();i++) {
				 System.out.println("text " + e.get(i)+" è del type "+d.get(i)+" con relevance di "+f.get(i)+" e score finale pari a "+g.get(i));
				 out.println("Il text " + e.get(i)+" è del type " + d.get(i)+" con relevance di " + f.get(i)+"e score finale pari a "+g.get(i)+"<br>");
				 }
		 
				 System.out.println("*********************************************************");
		 
		 */
		
				break;
		}
		out.println("</html>");
	
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}

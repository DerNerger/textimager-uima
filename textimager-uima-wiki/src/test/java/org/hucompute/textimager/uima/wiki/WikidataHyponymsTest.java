package org.hucompute.textimager.uima.wiki;

import org.apache.uima.UIMAException;
import org.apache.uima.fit.factory.AggregateBuilder;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.junit.Test;

import com.google.common.collect.Iterators;

import static org.junit.Assert.*;

import java.io.IOException;

import de.tudarmstadt.ukp.dkpro.core.io.jwpl.type.WikipediaLink;

public class WikidataHyponymsTest  {
	
	@Test
	public void getWikipediaLink() throws UIMAException, IOException{
		WikidataHyponyms WikidataHyponyms = new WikidataHyponyms();
		assertEquals(WikidataHyponyms.wikiDataFromWikipediaLink("https://de.wikipedia.org/wiki/Renaissance"), "Q4692");
		assertEquals(WikidataHyponyms.wikiDataFromWikipediaLink("Renaissance","de"), "Q4692");
		assertEquals(WikidataHyponyms.wikiDataFromWikipediaLink("Renaissance","de"), WikidataHyponyms.wikiDataFromWikipediaLink("https://de.wikipedia.org/wiki/Renaissance"));

		assertEquals(WikidataHyponyms.wikiDataFromWikipediaLink("https://en.wikipedia.org/wiki/Protagoras"), "Q169243");
		assertEquals(WikidataHyponyms.wikiDataFromWikipediaLink("Protagoras","en"), "Q169243");
		assertEquals(WikidataHyponyms.wikiDataFromWikipediaLink("Protagoras","en"), WikidataHyponyms.wikiDataFromWikipediaLink("https://en.wikipedia.org/wiki/Protagoras"));
	}
	
	
	@Test
	public void simpleExampleDE() throws UIMAException{
		JCas cas = JCasFactory.createText("Mona Lisa ist ein weltberühmtes Ölgemälde von Leonardo da Vinci aus der Hochphase der italienischen Renaissance Anfang des 16. Jahrhunderts.", "de");

		addWikipediaLink(cas, 32, 41, "https://de.wikipedia.org/wiki/%C3%96lmalerei", "internal", null);
		addWikipediaLink(cas, 46, 63, "https://de.wikipedia.org/wiki/Leonardo_da_Vinci", "internal", null);
		addWikipediaLink(cas, 72, 81, "https://de.wikipedia.org/wiki/Hochrenaissance", "internal", null);
		
		org.hucompute.textimager.uima.type.wikipedia.WikipediaLink wiki = new org.hucompute.textimager.uima.type.wikipedia.WikipediaLink(cas,100,111);
		wiki.setAnchor(null);
		wiki.setTarget("https://de.wikipedia.org/wiki/Renaissance");
		wiki.setLinkType("internal");
		wiki.addToIndexes();
		
		AggregateBuilder builder = new AggregateBuilder();
		builder.add(AnalysisEngineFactory.createEngineDescription(WikidataHyponyms.class));
		SimplePipeline.runPipeline(cas,builder.createAggregate());
		
		org.hucompute.textimager.uima.type.wikipedia.WikipediaLink [] wikiLinks = Iterators.toArray(JCasUtil.select(cas, org.hucompute.textimager.uima.type.wikipedia.WikipediaLink.class).iterator(), org.hucompute.textimager.uima.type.wikipedia.WikipediaLink.class);
		
		//Mahlerei
		assertEquals(wikiLinks[0].getWikiData(),"Q174705");
		assertArrayEquals(wikiLinks[0].getWikiDataHyponyms().toArray(), new String[]{"Q1231896", "Q26904132", "Q174705"});
		
		//Da Vinci
		assertEquals(wikiLinks[1].getWikiData(),"Q762");
		assertArrayEquals(wikiLinks[1].getWikiDataHyponyms().toArray(), new String[]{"Q5", "Q215627", "Q21070568", "Q762"});

		//Hochrenaissance
		assertEquals(wikiLinks[2].getWikiData(),"Q1474884");
		assertArrayEquals(wikiLinks[2].getWikiDataHyponyms().toArray(), new String[]{"Q32880", "Q968159", "Q1792644", "Q735", "Q2198855", "Q1474884"});

		//Renaisasnce
		assertEquals(wikiLinks[3].getWikiData(),"Q4692");
		assertArrayEquals(wikiLinks[3].getWikiDataHyponyms().toArray(), new String[]{"Q32880", "Q968159", "Q1792644", "Q735", "Q2198855", "Q4692"});
	}
	
	private void addWikipediaLink(JCas cas, int begin, int end, String target, String linkTyp, String anchor){
		WikipediaLink wiki = new WikipediaLink(cas,begin,end);
		wiki.setAnchor(anchor);
		wiki.setTarget(target);
		wiki.setLinkType(linkTyp);
		wiki.addToIndexes();
	}
	
}

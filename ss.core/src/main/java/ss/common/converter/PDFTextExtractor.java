/*
 * Created on Apr 11, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ss.common.converter;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.lucene.LucenePDFDocument;
import org.apache.pdfbox.util.PDFTextStripper;

/**
 * @author david
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Generation - Code and Comments
 */
class PDFTextExtractor {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(PDFTextExtractor.class);

	public PDFTextExtractor() {

		System.setProperty("log4j.configuration", "file:/c:\\log4j.xml");

	}

	public static void main(String[] args) {
		try {
			Document luceneDocument = LucenePDFDocument.getDocument(new File(
					"c:\\djatresume.doc.pdf"));
			Analyzer analyzer = new StandardAnalyzer();

			luceneDocument.add(new Field("id", "asdfasdf", Field.Store.YES,
					Field.Index.UN_TOKENIZED));
			File indexFile = new File("c:\\index");

			IndexWriter writer = null;
			try {
				writer = new IndexWriter(indexFile, analyzer, false);
			} catch (IOException e) {

				try {
					writer = new IndexWriter(indexFile, analyzer, true);
				} catch (IOException e1) {

				}

				writer.addDocument(luceneDocument);

				writer.optimize();
				writer.close();

			}
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}

	}

	public String extract(String filename) {
		String pdfText = null;
		try {
			PDDocument pdd = new PDDocument();
			File file = new File(filename);

			PDFTextStripper pdf = new PDFTextStripper();
			pdfText = pdf.getText(PDDocument.load(file));
			logger.info("pdftext: " + pdfText);

			pdd.close();

		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}

		return pdfText;
	}

}

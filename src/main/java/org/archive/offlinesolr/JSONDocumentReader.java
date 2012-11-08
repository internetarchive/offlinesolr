package org.archive.offlinesolr;

import org.apache.solr.core.CoreContainer;
import org.apache.solr.core.SolrCore;

import org.apache.solr.common.SolrInputDocument;

import org.apache.solr.update.DirectUpdateHandler2;

import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;

import java.util.*;
import java.io.IOException;
import java.io.FileInputStream;

import java.io.*;
import org.json.*;

/**
 * Reads text file contains multiple JSON records, one per line.
 */
public class JSONDocumentReader {
    BufferedReader reader;
    String nextLine;
        

    public JSONDocumentReader(String path) 
            throws IOException {
        this(new File(path));
    }

    public JSONDocumentReader(File file) 
            throws IOException {
        reader = new BufferedReader(new FileReader(file));
        nextLine = reader.readLine();
    }

    public Collection<SolrInputDocument> readAll() {
        ArrayList<SolrInputDocument> list = new ArrayList<SolrInputDocument>();

        while (hasNext()) 
            list.add(next());

        return list;
    }

    public boolean hasNext() {
        return nextLine != null;
    }

    public SolrInputDocument next() {
        SolrInputDocument doc = makeSolrDoc(nextLine);
        try {
            nextLine = reader.readLine();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        return doc;
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }

    private static SolrInputDocument makeSolrDoc(String jsonText) {
        try {
            JSONObject jobj = new JSONObject(jsonText); 

            SolrInputDocument doc = new SolrInputDocument();

            for (String key: JSONObject.getNames(jobj)){
                Object value = jobj.opt(key);
                if (value instanceof JSONArray) {
                    JSONArray array = (JSONArray)value;
                    for (int i=0; i<array.length(); i++) {
                        doc.addField(key, array.get(i));
                    }
                }
                else {
                    doc.addField(key, value);
                }
            }
            return doc;
        }
        catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
}

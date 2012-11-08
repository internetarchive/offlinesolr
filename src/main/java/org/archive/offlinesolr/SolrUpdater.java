package org.archive.offlinesolr;

import org.apache.solr.core.CoreContainer;
import org.apache.solr.core.SolrCore;

import org.apache.solr.common.SolrInputDocument;

import org.apache.solr.update.DirectUpdateHandler2;

import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;

import java.util.*;
import java.io.IOException;
import java.io.FileInputStream;

import org.json.*;

/**
 * Class to build/update solr index offline.
 *
 * Expects the following properties to be set.
 *
 *      solr.solr.home
 *      solr.solr.data
 *      solr-core
 *
 * The last one is requied only when using a multi-core solr.
 *
 * Works only with Solr 1.4.0.
 */
public class SolrUpdater {
    public static void main(String[] args) throws Exception {
        try {
            CoreContainer cc = new CoreContainer.Initializer().initialize();
            String core = System.getProperty("solr-core", "");
            EmbeddedSolrServer server = new EmbeddedSolrServer(cc, core);
            System.out.println("Core: " + core);

            Collection<SolrInputDocument> iter = new JSONDocumentReader(args[0]).readAll();
            server.add(iter);
            server.commit();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            System.exit(0);
        }
    }

    private static String readFile(String path) throws IOException {
        byte[] buf = new byte[1024 * 1024];
        StringBuffer out = new StringBuffer();
        int n;

        FileInputStream f = new FileInputStream(path);
        while ((n = f.read(buf)) >= 0) {
            out.append(new String(buf, 0, n));
        }
        return out.toString();
    }

    private static SolrInputDocument makeSolrDoc(String path) throws IOException, JSONException {
        JSONObject jobj = new JSONObject(readFile(path));
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
}

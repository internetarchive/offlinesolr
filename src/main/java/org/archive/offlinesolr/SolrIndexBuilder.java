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
public class SolrIndexBuilder {
    public static void main(String[] args) throws Exception {
        try {
            CoreContainer.Initializer initializer = new CoreContainer.Initializer();
            CoreContainer coreContainer = initializer.initialize();
            EmbeddedSolrServer server = new EmbeddedSolrServer(coreContainer, "");

            Collection<SolrInputDocument> iter = new JSONDocumentReader(args[0]).readAll();
            server.add(iter);
            server.commit();
            server.optimize();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            System.exit(0);
        }
    }
}

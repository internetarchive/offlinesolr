offlinesolr
===========

A simple tool to build a solr index offline. 

Rebuilding a Solr index is a hard task. This library makes that easier by building
the Solr index offline as a batch process. 

This works with Solr 3.6.x.

How to build
------------

    mvn package

This builds the jar and places it at `target/offline-solr-1.0-SNAPSHOT.jar`.

How to build an index
---------------------

This takes solr documents to be indexed in JSON format with one entry per line.

    java -Dsolr.solr.home=/etc/solr -Dsolr.data.dir=/tmp/new-solr-index -jar offline-solr-1.0-SNAPSHOT.jar solrdump.txt

Make sure that solr data dir can be overwritten by a system property. Your
`dataDir` setting in `solrconfig.xml` should look something like this:

    <dataDir>${solr.data.dir:/var/lib/solr/data}</dataDir>


package org.roboutik.dao.impl

import org.apache.solr.client.solrj.StreamingResponseCallback
import org.apache.solr.common.{SolrDocument, SolrDocumentList}
import org.slf4j.LoggerFactory

/**
  * Created by keech on 10/8/16.
  */
class SolrCallback extends StreamingResponseCallback() {
  lazy val log = LoggerFactory.getLogger(this.getClass)
  var maxResults=0L
  var counter =0L
  val start = System.currentTimeMillis
  var startTime = System.currentTimeMillis
  var finishTime = System.currentTimeMillis
  var docList = new SolrDocumentList()
  def streamSolrDocument(doc: SolrDocument) {
    docList.add(doc)
    counter+=1
    if (counter%10000==0){
      finishTime = System.currentTimeMillis
      log.debug(counter + " >> Last record : " + doc.getFieldValueMap + ", records per sec: " +
        100000.toFloat / ((finishTime - startTime).toFloat / 1000f) + ", This occured at: " + new java.util.Date())
      startTime = System.currentTimeMillis
    }
  }

  override def streamDocListInfo(numFound: Long, start: Long, maxScore: java.lang.Float) {
    if(numFound> 0 ) {
      log.debug(s"NumbFound: $numFound start: $start maxScore: $maxScore")
      log.debug(s"${new java.util.Date()}")
      startTime = System.currentTimeMillis
      maxResults = numFound
    }
  }
}
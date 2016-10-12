package org.roboutik.dao

/**
  * Created by Keech on 09/10/2016.
  */


import org.apache.solr.client.solrj.{SolrQuery, SolrServer}
import org.apache.solr.common.SolrDocumentList
import org.roboutik.dao.impl.SolrCallback

import scala.util.parsing.json.JSON


/**
  * All Index implementations need to extend this trait.
  */
trait IndexDAO {

  val rowLimit = 1000

  def streamIndex(server:SolrServer, query: SolrQuery, solrCallback: SolrCallback): SolrDocumentList

  def getConnection(collection:String, connectionDetails: String*):SolrServer

  def getRowKeysForQuery(query: SolrQuery, solrServer: SolrServer,limit: Option[Int]= Some(rowLimit),solrCallback: SolrCallback): Option[List[String]]

  def configureKerberosSecurity(jaasLocation: String)

  def getKerberosToken(solrUrl: String, jaasLocation: String):Option[String]

}
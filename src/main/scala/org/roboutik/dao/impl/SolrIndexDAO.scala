package org.roboutik.dao.impl

import org.apache.solr.client.solrj.impl.{CloudSolrServer, HttpSolrServer}
import org.roboutik.dao.IndexDAO
import org.apache.solr.client.solrj.request.DelegationTokenRequest
import org.apache.solr.client.solrj.{SolrQuery, SolrServer}
import org.apache.solr.common.SolrDocumentList
import org.slf4j.LoggerFactory

import scala.collection.convert.decorateAsScala._
import scala.util.Try

/**
  * Created by Keech on 12/10/2016.
  */
class SolrIndexDAO extends IndexDAO {
  var solrServer: SolrServer = _
  var cloudServer: CloudSolrServer = _
  final val id = "id"
  lazy val log = LoggerFactory.getLogger(this.getClass)

  override def streamIndex(server:SolrServer, query: SolrQuery, solrCallback: SolrCallback):SolrDocumentList ={
    log.debug("Streaming results and returning documents")
    server.queryAndStreamResponse(query,solrCallback)
    solrCallback.docList
  }


  override def getConnection(collection:String, connectionDetails: String*):SolrServer = {
    log.debug("Creating Solr ")
    val server = new CloudSolrServer(connectionDetails(0))
    server.setDefaultCollection(collection)
    log.debug(s"Solr Server created at ${server.ping()}")
    server.setDefaultCollection(collection)
    server
  }

  def getKerberizedConnection(collection:String, token:String,connectionDetails: String):SolrServer = {
    log.debug("Creating Solr ")
    val server = new CloudSolrServer(connectionDetails)
    log.debug(s"Solr Server created at ${server.ping()}")
    server.setDefaultCollection(collection)
    System.setProperty(HttpSolrServer.DELEGATION_TOKEN_PROPERTY, token)
    server
  }

  def getRowKeysForQuery(query: SolrQuery, solrServer: SolrServer,limit: Option[Int], solrCallback: SolrCallback): Option[List[String]] = {
    val q  = if(limit.isDefined){query.setRows(limit.get)}else query
    q.remove("fl")
    q.addField(id)
    streamIndex(solrServer:SolrServer, q: SolrQuery, solrCallback: SolrCallback)
    Try(solrCallback.docList.asScala.map(i => i.get(id).toString).toList).toOption
  }


  override def configureKerberosSecurity(jaasLocation: String): Unit ={
    System.setProperty("java.security.auth.login.config", jaasLocation)
  }

  override def getKerberosToken(solrUrl: String, jaasLocation: String):Option[String] ={
    configureKerberosSecurity(jaasLocation)
    val solrHttpServer = new CloudSolrServer(solrUrl)
    solrHttpServer.setDefaultCollection("kraken")
    val getToken = new DelegationTokenRequest.Get()
    Try{
      val getTokenResponse = getToken.process(solrHttpServer)
      val t = getTokenResponse.getDelegationToken
      log.debug("Token details: {}" ,t)
      System.setProperty(HttpSolrServer.DELEGATION_TOKEN_PROPERTY, t)
      t
    }.toOption
  }

  def setDelegationTokenSysProp(token: Option[String])={
    token match {
      case Some(t) => System.setProperty(HttpSolrServer.DELEGATION_TOKEN_PROPERTY, t)
      case None =>
    }
  }

}

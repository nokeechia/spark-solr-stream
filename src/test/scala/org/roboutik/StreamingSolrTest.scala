package org.roboutik

package au.org.ala.perfomance

import org.apache.solr.client.solrj.impl.{CloudSolrServer, HttpSolrServer}

import scala.collection.JavaConversions._
import collection.immutable._
import org.apache.solr.client.solrj.StreamingResponseCallback
import java.lang.Float

import org.apache.solr.common.SolrDocument
import org.apache.solr.common._
import org.apache.solr.common.params._
import org.scalatest.Ignore
import com.typesafe.config.{Config, ConfigFactory, ConfigObject, ConfigValue}
/*
 * Copyright (C) 2012 Atlas of Living Australia
 * All Rights Reserved.
 *
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 */
/**
  * Test the performance of the stream solr download
  */
@Ignore
object StreamingSolrTest {
  def main(args:Array[String]){
    val config: Config = ConfigFactory.load()
    val solrUrl = config.getString("solr-url")
    val collection = config.getString("collection")
    val server = new HttpSolrServer(solrUrl)
    val params = HashMap(
      "collectionName" -> collection,
      "q" -> "*:*",
      "start" -> "0",
      "rows" -> Int.MaxValue.toString,
      "fl" -> "id",
      "sort" -> "versionTimestamp asc")

    val solrParams = new MapSolrParams(params)
    val solrCallback = new StreamingResponseCallback() {
      var maxResults=0l
      var counter =0l
      val start = System.currentTimeMillis
      var startTime = System.currentTimeMillis
      var finishTime = System.currentTimeMillis
      var docList = new SolrDocumentList()
      def streamSolrDocument(doc: SolrDocument) {
        docList.add(doc)
        counter+=1
        if (counter%10000==0){
          finishTime = System.currentTimeMillis
          println(counter + " >> Last record : " + doc.getFieldValueMap + ", records per sec: " +
            100000.toFloat / (((finishTime - startTime).toFloat) / 1000f) + ", This occured at: " +new java.util.Date())
          startTime = System.currentTimeMillis
        }
      }

      def streamDocListInfo(numFound: Long, start: Long, maxScore: Float) {
        import scala.collection.JavaConverters._
        import scala.collection.JavaConversions._
        println("NumbFound: " + numFound +" start: " +start + " maxScore: " +maxScore)
        println(new java.util.Date())
        startTime = System.currentTimeMillis
        //exit(-2)
        maxResults = numFound
        println(s"Doc list size: ${docList.length}")
        docList.asScala.foreach(i => println(s"Doc: ${i}"))
      }
    }
    println(new java.util.Date())
    server.queryAndStreamResponse(solrParams, solrCallback)
    println(solrCallback.docList.length)
  }
}

package org.roboutik

import com.typesafe.config.{Config, ConfigFactory}
import org.apache.solr.client.solrj.impl.HttpSolrServer
import org.apache.solr.common.params._
import org.apache.spark.{SparkConf, SparkContext}
import org.roboutik.dao.impl.SolrCallback

import scala.collection.JavaConversions._
import scala.collection.immutable._
import org.apache.spark.SparkContext._
import org.roboutik.dao.impl.query._
/**
  * Created by Keech on 09/10/2016.
  */

object Main {

  def  main(args: Array[String]):Unit = {
    val conf = new SparkConf()
    conf.setMaster("local[2]").setAppName("spark-with-solr")
    val sc = new SparkContext(conf)

    val config: Config = ConfigFactory.load()
    val solrUrl = if(config.hasPath("solr-url")){
      config.getString("solr-url")
    } else{
      throw  new IllegalArgumentException("no solr-url defined in the resources directory")
    }
    val collection = if(config.hasPath("collection")) {
      config.getString("collection")
    }else{
      throw  new IllegalArgumentException("no collection defined in the resources directory")
    }

    val params = HashMap(
      "collectionName" -> collection,
      "q" -> "*:*",
      "start" -> "0",
      "rows" -> Int.MaxValue.toString,
      "fl" -> "id",
      "sort" -> "versionTimestamp asc")



    val pageBuckets = List((0,1000),(1001,2001),(3000,4000),(4001,5000),(5001,6000),(6001,7000),(7001,8000),(8001,9000))
    val pageBucketsRDD = sc.parallelize(pageBuckets,1)
    val documents = pageBucketsRDD.mapPartitions{ it =>
      val solrParams = new MapSolrParams(params)
      val server = new HttpSolrServer(solrUrl)
      val solrCallback = new SolrCallback()
      it.map{ i =>
        val query = new OrderQueryBuilder().setDocumentRange(DocumentRange(i._1,i._2)).setAllFieldsFlag(true).
          setSort(SortParameters(ASC, "versionTimestamp")).setFieldList("id").build()
        println(query.toString)
        //println(new java.util.Date())
        server.queryAndStreamResponse(solrParams,solrCallback)
        //println(solrCallback.docList.length)
        solrCallback.docList
      }
    }
    val ids = documents.flatMap{it=>
      it.map(i => i.toString)
    }
    println(s"Documents retrieved: ${ids.count()}")
    ids.collect().foreach(i => println(s"Order ID retrieved: $i"))

  }
}

package org.roboutik.dao

/**
  * Created by Keech on 09/10/2016.
  */

import scala.collection.mutable.ArrayBuffer
import java.util.Date
import org.slf4j.LoggerFactory
import java.io.{File, FileWriter, OutputStream}

import scala.util.parsing.json.JSON


/**
  * All Index implementations need to extend this trait.
  */
trait IndexDAO {

  val logger = LoggerFactory.getLogger("IndexDAO")

  def getRowKeysForQuery(query: String, limit: Int = 1000): Option[List[String]]

  def streamIndex(proc: java.util.Map[String,AnyRef] => Boolean, fieldsToRetrieve:Array[String], query:String, filterQueries: Array[String], sortFields: Array[String],multivaluedFields: Option[Array[String]] = None)


}
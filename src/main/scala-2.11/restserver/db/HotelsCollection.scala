package restserver.db

/**
  * Created by anton on 10.10.16.
  */

import reactivemongo.bson.{BSONDocument, BSONObjectID}
import reactivemongo.api.collections.bson.BSONCollection

import scala.concurrent.ExecutionContext
import Hotel._
import reactivemongo.api.Cursor


object HotelsCollection {
  import MongoDB._

  val collection = db[BSONCollection]("hotel_attributes")

  def save(hotel: Hotel)(implicit ec: ExecutionContext) =
    collection.insert(hotel).map(_ => Created(hotel.id))

  def findById(id: String)(implicit ec: ExecutionContext) =
    collection.find(queryById(id)).one[Hotel]

  def deleteById(id: String)(implicit ec: ExecutionContext) =
    collection.remove(queryById(id)).map(_ => Deleted)

  def find()(implicit ec: ExecutionContext) =
    collection.find(emptyQuery).cursor[Hotel]().collect[List](Int.MaxValue, Cursor.FailOnError[List[Hotel]]())

  private def queryById(id: String) = BSONDocument("_id" -> BSONObjectID.parse(id).get)

  private def emptyQuery = BSONDocument()
}

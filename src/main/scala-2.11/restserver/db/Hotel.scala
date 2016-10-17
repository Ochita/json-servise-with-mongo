package restserver.db
import javafx.geometry.Point2D

import reactivemongo.bson._

/**
  * Created by anton on 10.10.16.
  */

case class Location (x: Double, y: Double) extends Point2D (x, y)

case class Hotel(id:String, location: Location)

object Hotel {

  implicit object HotelBSONReader extends BSONDocumentReader[Hotel] {
    def read(doc: BSONDocument): Hotel = {
      val location = doc.getAs[List[Double]]("location").get
      Hotel(
        id = doc.getAs[BSONObjectID]("_id").get.stringify,
        location = Location(location.head, location.last)
      )
    }
  }

  implicit object HotelBSONWriter extends BSONDocumentWriter[Hotel] {
    def write(hotel: Hotel): BSONDocument =
      BSONDocument(
        "_id" -> BSONObjectID.parse(hotel.id).get,
        "location" -> List(hotel.location.getX, hotel.location.getY)
      )
  }
}

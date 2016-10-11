package restserver.db
import reactivemongo.bson._

/**
  * Created by anton on 10.10.16.
  */

case class Hotel(id:String, sip_prefix:String)

object Hotel {

  implicit object HotelBSONReader extends BSONDocumentReader[Hotel] {
    def read(doc: BSONDocument): Hotel = {
      Hotel(
        id = doc.getAs[BSONObjectID]("_id").get.stringify,
        sip_prefix = doc.getAs[String]("sip_prefix").get
      )
    }
  }

  implicit object HotelBSONWriter extends BSONDocumentWriter[Hotel] {
    def write(hotel: Hotel): BSONDocument =
      BSONDocument(
        "_id" -> BSONObjectID.parse(hotel.id).get,
        "sip_prefix" -> hotel.sip_prefix
      )
  }
}

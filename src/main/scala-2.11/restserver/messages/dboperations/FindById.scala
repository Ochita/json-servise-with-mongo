package restserver.messages.dboperations

/**
  * Created by anton on 14.10.16.
  */
case class FindById(collection: String, id: String) extends DBOperation

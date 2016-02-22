package models

import org.joda.time.DateTime
import scalikejdbc._
import scalikejdbc.SQLInterpolation._

case class Task(id: Int, title: String, content: String, createdAt: DateTime)

object Task extends SQLSyntaxSupport[Task] {
  override val tableName = "tasks"
  
  def apply(rs: WrappedResultSet):Task = Task(
    rs.int("id"),
    rs.string("title"),
    rs.string("content"),
    rs.jodaDateTime("createdAt")
  )
  
  def create(title: String, content: String)(implicit dbSession: DBSession = AutoSession) = {
    sql"insert into tasks (title, content) values (${title}, ${content})".update.apply()
  }
}

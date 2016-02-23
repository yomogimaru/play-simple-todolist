package models

import org.joda.time.DateTime
import scalikejdbc._

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
  
  def findById(id: Int): Option[Task] = {
    DB readOnly { implicit session =>
      sql"select id, title, content, createdAt from tasks where id = ${id}".map(Task(_)).single.apply()
    }
  }
  
  def getAll: List[Task] = {
    DB readOnly { implicit session =>
      sql"select id, title, content, createdAt from tasks".map(Task(_)).list.apply().sortWith(
        (a, b) => a.createdAt.isAfter(b.createdAt)
      )
    }
  }
  
  def delete(id: Int)(implicit dbSession: DBSession = AutoSession) = {
    sql"delete from tasks where id = ${id};".update.apply()
  }
}

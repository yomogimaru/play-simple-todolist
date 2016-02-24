package models

import org.joda.time.DateTime
import scalikejdbc._

// タスク1つのデータ構造
case class Task(id: Int, title: String, content: String, createdAt: DateTime)

// DBでのタスク管理
object Task extends SQLSyntaxSupport[Task] {
  override val tableName = "tasks"
  
  // case classへのマッピング
  def apply(rs: WrappedResultSet):Task = Task(
    rs.int("id"),
    rs.string("title"),
    rs.string("content"),
    rs.jodaDateTime("createdAt")
  )
  
  // タスク新規作成
  def create(title: String, content: String)(implicit dbSession: DBSession = AutoSession) = {
    sql"insert into tasks (title, content) values (${title}, ${content})".update.apply()
  }
  
  // 1つのタスクをidで検索
  def findById(id: Int): Option[Task] = {
    DB readOnly { implicit session =>
      sql"select id, title, content, createdAt from tasks where id = ${id}".map(Task(_)).single.apply()
    }
  }
  
  // すべてのタスクを取得　新しいものが前(日付で降順)
  def getAll: List[Task] = {
    DB readOnly { implicit session =>
      sql"select id, title, content, createdAt from tasks".map(Task(_)).list.apply().sortWith(
        (a, b) => a.createdAt.isAfter(b.createdAt)
      )
    }
  }
  
  // タスクを1つ削除
  def delete(id: Int)(implicit dbSession: DBSession = AutoSession) = {
    sql"delete from tasks where id = ${id};".update.apply()
  }
}

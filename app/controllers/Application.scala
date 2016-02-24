package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.routing._

import models._

class Application extends Controller {

  // タスク一覧
  def tasks = Action {
    Ok(views.html.tasks(Task.getAll))
  }
  
  // タスクの詳細表示(対象が見つからなかったらnot found)
  def show(id: Int) = Action {
    Task.findById(id) match {
      case Some(task) => Ok(views.html.show(task))
      case _          => NotFound("<h1>Task not found</h1>")
    }
  }

  // 新規作成画面
  def createFormView = Action {
    Ok(views.html.create(createForm))
  }
  
  // タスクを作成し、タスク一覧へ移動
  def create = Action { implicit request =>
    createForm.bindFromRequest.fold(
      error => BadRequest("Error"),
      {
        case CreateForm(t, c) => {
          Task.create(t, c)
          Redirect(routes.Application.tasks)
        }
      }
    )
  }
  
  // フォームのデータとcase classとのマッピング
  val createForm = Form(
    mapping(
      "title"   -> text,
      "content" -> text
    )(CreateForm.apply)(CreateForm.unapply)
  )
  
  // タスクの削除(対象が見つからなかったらnot found)
  def delete(id: Int) = Action {
    Task.findById(id) match {
      case Some(task) => {
        Task.delete(task.id)
        Redirect(routes.Application.tasks)
      }
      case _ => NotFound("<h1>Task not found</h1>")
    }
  }
  
  // JSルーティング(現在deleteのみ)
  def javascriptRoutes = Action { implicit request =>
    Ok(
      JavaScriptReverseRouter("jsRoutes")(
        routes.javascript.Application.delete
      )
    ).as("text/javascript")
  }
}

// フォーム情報を受け取るデータ構造
case class CreateForm(title: String, content:String)

package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.routing._

import models._

class Application extends Controller {

  def tasks = Action {
    Ok(views.html.tasks(Task.getAll))
  }
  
  def show(id: Int) = Action {
    Task.findById(id) match {
      case Some(task) => Ok(views.html.show(task))
      case _          => NotFound("<h1>Task not found</h1>")
    }
  }

  def createFormView = Action {
    Ok(views.html.create(createForm))
  }
  
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
  
  val createForm = Form(
    mapping(
      "title"   -> text,
      "content" -> text
    )(CreateForm.apply)(CreateForm.unapply)
  )
  
  def delete(id: Int) = Action {
    Task.findById(id) match {
      case Some(task) => {
        Task.delete(task.id)
        Redirect(routes.Application.tasks)
      }
      case _ => NotFound("<h1>Task not found</h1>")
    }
  }
  
  def javascriptRoutes = Action { implicit request =>
    Ok(
      JavaScriptReverseRouter("jsRoutes")(
        routes.javascript.Application.delete
      )
    ).as("text/javascript")
  }
}

case class CreateForm(title: String, content:String)

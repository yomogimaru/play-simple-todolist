package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._

import models._

class Application extends Controller {

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  def tasks = Action {
    Ok(views.html.tasks(List("hoge", "fuga")))
  }

  def createFormView = Action {
    Ok(views.html.create(createForm))
  }
  
  def create = Action { implicit request =>
    createForm.bindFromRequest.fold(
      error => {
        BadRequest("Error")
      },
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
      "title"    -> text,
      "contents" -> text
    )(CreateForm.apply)(CreateForm.unapply)
  )
}

case class CreateForm(title: String, content:String)

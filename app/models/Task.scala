package models

import org.joda.time.DateTime

case class Task(id: Int, title: String, content: String, createdAt: DateTime)

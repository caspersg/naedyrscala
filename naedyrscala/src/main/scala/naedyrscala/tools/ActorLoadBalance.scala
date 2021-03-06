/*
Copyright 2010 naedyr@gmail.com

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0
       
Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package naedyrscala.tools

import scala.actors.Future
import scala.actors.Actor
import scala.actors.Actor._

object ActorLoadBalance {

  def apply(theActors: Seq[Actor]) = actor {
    var actors = theActors
    var latest = 0
    loop {
      react {
        case x: Exit =>
          actors foreach {
            _ ! x
          }
          exit
        case x =>
          actors(latest) ! x
          latest = nextActor(actors, latest)
      }
    }
  }
  def nextActor(actors: Seq[Actor], last: Int): Int = {
    (last + 1) % actors.size
  }
}

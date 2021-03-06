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

import naedyrscala.tools.Parallel._

import org.junit._
import Assert.assertEquals

class TransactionTest {
  @Test
  def testOptomisticOptomistic() {
    test(TransactionOptimistic(), TransactionOptimistic())
  }

  @Test
  def testOptomisticPessimistic() {
    test(TransactionOptimistic(), TransactionPessimistic())
  }

  @Test
  def testPessimisticOptomistic() {
    test(TransactionPessimistic(), TransactionOptimistic())
  }

  @Test
  def testPessimisticPessimistic() {
    test(TransactionPessimistic(), TransactionPessimistic())
  }

  def test(trans: Transaction, transB: Transaction) {

    trans {
      println("do")
      transB {
        println("some")
      }
      println("stuff")
    }

    var start = System.currentTimeMillis()
    trans {
      transB {
        println("do")
        if (System.currentTimeMillis() < start + 50) {
          trans.retry()
        }
      }
      println("some")
      println("stuff")
    }

    (1 to 100) pforeach { x =>
      trans {
        println("one " + x)
        transB {
          println("two " + x)
          println("three " + x)
        }
      }
    }
    (1 to 100) pforeach { x =>
      transB {
        println("one " + x)
        trans {
          println("two " + x)
          println("three " + x)
        }
      }
    }
  }
}
/**
 * Negation
 * α | ~α
 * --x----
 * f | t
 * u | u
 * t | f
 *
 * Implication
 * → | f u t
 * --x-------
 * f | t t t
 * u | u u t
 * t | f u t
 *
 * Disjunction
 * v | f u t
 * --x-------
 * f | f u t
 * u | u u t
 * t | t t t
 *
 * Conjunction
 * ∧ | f u t
 * --x-------
 * f | f f f
 * u | f u u
 * t | f u t
 *
 * Equivalence
 * ≡ | f u t
 * --x-------
 * f | t u f
 * u | u u u
 * t | f u t
 */
object KleenePrettyPrinter {
  val PIPE = "|"
  val SEP = " " + PIPE + " "
  val SPACE = " "
  val TITLE = "f u t"

  def main(args: Array[String]): Unit = {
    printNegationTable
    printImplicationTable
    printDisjunctionTable
    printConjunctionTable
    printEquivalenceTable
  }

  private def printNegationTable: Unit = {
    printTitle("α", "~α")
    for (a <- K3.stream) {
      println(a + SEP + ~a)
    }
    println()
  }

  private def printImplicationTable: Unit = {
    printTitle("→", TITLE)
    K3.stream.foreach((a: K3) => {
      print(a + SEP)
      K3.stream.foreach((b: K3) => {
        print(a → b + SPACE)
      })
      println()
    })
    println()
  }

  private def printDisjunctionTable: Unit = {
    printTitle("v", TITLE)
    K3.stream.foreach((a: K3) => {
      print(a + SEP)
      K3.stream.foreach((b: K3) => {
        print((a v b) + SPACE)
      })
      println()
    })
    println()
  }

  private def printConjunctionTable: Unit = {
    printTitle("∧", TITLE)
    K3.stream.foreach((a: K3) => {
      print(a + SEP)
      K3.stream.foreach((b: K3) => {
        print((a ∧ b) + SPACE)
      })
      println()
    })
    println()
  }

  private def printEquivalenceTable: Unit = {
    printTitle("≡", TITLE)
    K3.stream.foreach((a: K3) => {
      print(a + SEP)
      K3.stream.foreach((b: K3) => {
        print((a ≡ b) + SPACE)
      })
      println()
    })
    println()
  }

  private def printTitle(title1: String, title2: String): Unit = {
    println(title1 + SEP + title2)
    for (i <- 0 to title1.length + SEP.length + title2.length) {
      if (i == title1.length + 1) print("x")
      else print("-")
    }
    println()
  }
}

sealed trait K3 {
  /**
   * Negation operator
   *
   * @return
   */
  def unary_~ = this match {
    case Falsity => Truth
    case Indefiniteness => Indefiniteness
    case Truth => Falsity
  }

  /**
   * Implication operator
   *
   * @param that
   * @return
   */
  def →(that: K3): K3 = (this, that) match {
    case (Falsity, Falsity) => Truth
    case (Falsity, Indefiniteness) => Truth
    case (Falsity, Truth) => Truth
    case (Indefiniteness, Falsity) => Indefiniteness
    case (Indefiniteness, Indefiniteness) => Indefiniteness
    case (Indefiniteness, Truth) => Truth
    case (Truth, Falsity) => Falsity
    case (Truth, Indefiniteness) => Indefiniteness
    case (Truth, Truth) => Truth
  }

  /**
   * Disjunction operator
   *
   * @param that
   * @return
   */
  def v(that: K3): K3 = (this, that) match {
    case (Falsity, Falsity) => Falsity
    case (Falsity, Indefiniteness) => Indefiniteness
    case (Falsity, Truth) => Truth
    case (Indefiniteness, Falsity) => Indefiniteness
    case (Indefiniteness, Indefiniteness) => Indefiniteness
    case (Indefiniteness, Truth) => Truth
    case (Truth, Falsity) => Truth
    case (Truth, Indefiniteness) => Truth
    case (Truth, Truth) => Truth
  }

  def | = v _

  /**
   * Conjunction operator
   *
   * @param that
   * @return
   */
  def ∧(that: K3): K3 = (this, that) match {
    case (Falsity, Falsity) => Falsity
    case (Falsity, Indefiniteness) => Falsity
    case (Falsity, Truth) => Falsity
    case (Indefiniteness, Falsity) => Falsity
    case (Indefiniteness, Indefiniteness) => Indefiniteness
    case (Indefiniteness, Truth) => Indefiniteness
    case (Truth, Falsity) => Falsity
    case (Truth, Indefiniteness) => Indefiniteness
    case (Truth, Truth) => Truth
  }

  def & = ∧ _

  /**
   * Equivalence operator
   * @param that
   * @return
   */
  def ≡(that: K3): K3 = (this, that) match {
    case (Falsity, Falsity) => Truth
    case (Falsity, Indefiniteness) => Indefiniteness
    case (Falsity, Truth) => Falsity
    case (Indefiniteness, Falsity) => Indefiniteness
    case (Indefiniteness, Indefiniteness) => Indefiniteness
    case (Indefiniteness, Truth) => Indefiniteness
    case (Truth, Falsity) => Falsity
    case (Truth, Indefiniteness) => Indefiniteness
    case (Truth, Truth) => Truth
  }
}

case object Falsity extends K3 {
  override def toString: String = "f"
}

case object Indefiniteness extends K3 {
  override def toString: String = "u"
}

case object Truth extends K3 {
  override def toString: String = "t"
}

case object K3 {
  def stream: LazyList[K3] = Falsity #:: Indefiniteness #:: Truth #:: LazyList.empty[K3];
}


/**
 * Reference: https://filozof.uni.lodz.pl/prac/gm/papers/GM74.pdf
 */
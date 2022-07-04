package common.model

final case class Height(value: Int) extends AnyVal {
  def incr: Height = Height(value + 1)
}

object Height {
  val genesis = Height(0)
}

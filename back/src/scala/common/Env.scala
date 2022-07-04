package common

object Env {

  def getEnvKey[T](key: String, mapper: String => T, fallback: Option[T]): T =
    sys.env
      .get(key)
      .map(mapper)
      .orElse(fallback)
      .getOrElse(throw new Exception(s"$key not found in env"))

  def convertToInt(key: String, value: String): Int =
    value.toIntOption.getOrElse(
      throw new Exception(s"$key should be an Int but got $value")
    )

  def getString(key: String): String =
    getEnvKey(key, identity, None)

  def getString(key: String, default: String): String =
    getEnvKey(key, identity, Some(default))

  def getInt(key: String): Int =
    getEnvKey(key, convertToInt(key, _), None)

  def getInt(key: String, default: Int): Int =
    getEnvKey(key, convertToInt(key, _), Some(default))

}

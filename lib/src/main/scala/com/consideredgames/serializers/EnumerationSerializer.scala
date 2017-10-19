package com.consideredgames.serializers

import org.json4s.JsonAST.{JField, JObject, JString, JValue}
import org.json4s.{Formats, MappingException, Serializer, TypeInfo}

import scala.reflect.ClassTag


class EnumerationSerializer[E <: Enumeration: ClassTag](enum:E, key:Option[String] = None) extends Serializer[E#Value] {
  val EnumerationClass = classOf[E#Value]
  val enumKey = key.getOrElse(enum.getClass.getName)

  private def getEnumerationClass(e:E#Value) = e.getClass.getField("$outer").get(e).getClass

  def deserialize(implicit format: Formats):PartialFunction[(TypeInfo,JValue),E#Value] = new PartialFunction[(TypeInfo,JValue),E#Value] {

    def isDefinedAt(typeAndValue:(TypeInfo,JValue)):Boolean = typeAndValue match {
      case (TypeInfo(EnumerationClass,_),JObject(List(JField("enum",JString(e)),_))) if e == enumKey => true
      case _ => false
    }

    def apply(typeAndValue:(TypeInfo,JValue)):E#Value = typeAndValue match {
      case (TypeInfo(EnumerationClass,_),JObject(List(JField("enum",JString(e)),JField("value",JString(v))))) if e == enumKey => {
    	  enum.withName(v)
      }
      case (TypeInfo(EnumerationClass,_),v) => throw new MappingException("Can't convert " + v + " to enum "+ enum.getClass.getName)
      case other => throw new MappingException("Can't convert " + other + " to " + enum.getClass.getName)
    }
  }

  def serialize(implicit format:Formats):PartialFunction[Any,JValue] = new PartialFunction[Any,JValue] {

    def isDefinedAt(x:Any):Boolean = x match {
      case e:E#Value => getEnumerationClass(e) == enum.getClass
      case other => false
    }

    def apply(x:Any):JValue = x match {
      case e:E#Value if getEnumerationClass(e) == enum.getClass => write(e)
      case other:E#Value => throw new IllegalArgumentException("can't serialize enum " + other + " of type " + getEnumerationClass(other))
      case other:AnyRef => throw new IllegalArgumentException("can't serialize object " + other + " of type " + other.getClass.getName)
    }
  }

  def write(e:E#Value):JObject = {
		  JObject(List(JField("enum",JString(enumKey)),JField("value",JString(e.toString))))
  }

}

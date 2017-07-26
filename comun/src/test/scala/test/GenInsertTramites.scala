package test

import java.io.{File, PrintWriter}
import java.util.StringTokenizer

import org.scalatest.FlatSpec

import scala.io.Source

/**
  * Created by utaladriz on 03-07-17.
  */
class GenInsertTramites  extends FlatSpec {

  //ID;NOMBRE;MINISTERIO;SUBSECRETARIA;INSTITUCION;URL;DESCRIPCION

  case class SubseHash (id : Int, instituciones : scala.collection.mutable.HashMap[String, Int])
  case class MinHash (id : Int, subses : scala.collection.mutable.HashMap[String, SubseHash])



  "probando" should "true" in {
       var minIdx = 0
       var subseIdx = 0
       var instIdx = 0
       var tramIdx = 0
       var ministerioAnterior = ""
       var subseAnterior = ""
       var instAnterior = ""

       val ministerios = scala.collection.mutable.ListBuffer.empty[String]
       val subses = scala.collection.mutable.ListBuffer.empty[String]
       val instituciones = scala.collection.mutable.ListBuffer.empty[String]
       val tramites = scala.collection.mutable.ListBuffer.empty[String]
       val source = Source.fromFile("tramites/tramites.csv")
       val minHash = scala.collection.mutable.HashMap.empty[String, MinHash]

       for (linea <- source.getLines()){
         tramIdx = tramIdx +1
         println(tramIdx)

         val st = new StringTokenizer(linea, ";")
         val codigo = st.nextToken()
         val nombre = st.nextToken()
         val ministerio = st.nextToken().trim
         val subsecretaria = st.nextToken().trim
         val institucion = st.nextToken().trim
         val descripcion = st.nextToken()
         val url = if (st.hasMoreTokens) Some("'"+st.nextToken()+"'") else Some("NULL")

         if (minHash.get(ministerio).isEmpty){
           minIdx = minIdx + 1
           minHash += (ministerio ->  MinHash(minIdx, scala.collection.mutable.HashMap.empty[String, SubseHash]))
           ministerios += "INSERT INTO ministerio VALUES ("+minIdx+", '"+ministerio+"', true);"
         }
         val mh = minHash.get(ministerio).get
         if (mh.subses.get(subsecretaria).isEmpty){
           subseIdx = subseIdx + 1
           mh.subses  += (subsecretaria-> SubseHash(subseIdx, scala.collection.mutable.HashMap.empty[String, Int]))
           subses += "INSERT INTO subsecretaria VALUES ("+subseIdx+","+mh.id+",'"+subsecretaria+"', true);"
         }

       val sh = mh.subses.get(subsecretaria).get
         if (sh.instituciones.get(institucion).isEmpty){
           instIdx = instIdx + 1
           sh.instituciones += (institucion -> instIdx)
           instituciones += "INSERT INTO institucion VALUES ("+instIdx+","+sh.id+",'"+institucion+"', true);"
         }
         val ih = sh.instituciones.get(institucion).get
         tramites += "INSERT INTO tipo_tramite VALUES ("+tramIdx+",'"+nombre+"','"+descripcion+"',"+ih+",'"+codigo+"',"+url.get+", true);"

         println(s"$ministerio/$subsecretaria/$institucion $minIdx/$subseIdx/$instIdx $tramIdx")
       }
    var pw = new PrintWriter(new File("tramites/ministerios.sql" ))
    for (sql <- ministerios)
      pw.println(sql)
    pw.close
    pw = new PrintWriter(new File("tramites/subsecretarias.sql" ))
    for (sql <- subses)
      pw.println(sql)
    pw.close
    pw = new PrintWriter(new File("tramites/instituciones.sql" ))
    for (sql <- instituciones)
      pw.println(sql)
    pw.close
    pw = new PrintWriter(new File("tramites/tramites.sql" ))
    for (sql <- tramites)
      pw.println(sql)
    pw.close



    source.close
  }

}
